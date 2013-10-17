/*******************************************************************************
 * Copyright 2012 momock.com
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.momock.http;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.zip.GZIPInputStream;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;

import com.momock.event.Event;
import com.momock.event.EventArgs;
import com.momock.service.IAsyncTaskService;
import com.momock.service.IUITaskService;
import com.momock.util.Convert;
import com.momock.util.FileHelper;
import com.momock.util.Logger;

public class HttpSession{
			
	public static final int STATE_WAITING = 0;
	public static final int STATE_STARTED = 1;
	public static final int STATE_HEADER_RECEIVED = 2;
	public static final int STATE_CONTENT_RECEIVING = 3;
	public static final int STATE_CONTENT_RECEIVED = 4;
	public static final int STATE_ERROR = 5;
	public static final int STATE_FINISHED = 6;

	public static class DownloadInfo{
		long downloadedLength = 0;
		long contentLength = -1;
		DownloadInfo(long downloadedLength, long contentLength){
			this.downloadedLength = downloadedLength;
			this.contentLength = contentLength;
		}
		public long getDownloadedLength() {
			return downloadedLength;
		}
		public long getContentLength() {
			return contentLength;
		}
	}
	public static class StateChangedEventArgs extends EventArgs {
		int state;
		HttpSession session;

		public StateChangedEventArgs(int state, HttpSession session) {
			this.state = state;
			this.session = session;
		}

		public int getState() {
			return state;
		}

		public HttpSession getSession() {
			return session;
		}
	}

	HttpClient httpClient;
	String url;
	long downloadedLength = 0;
	long contentLength = -1;
	Throwable error = null;
	File file = null;
	File fileData = null;
	File fileInfo = null;
	int state = STATE_WAITING;
	int statusCode = 0;
	HttpRequestBase request = null;
	boolean downloadMode = false;
	byte[] result = null;
	IUITaskService uiTaskService;
	IAsyncTaskService asyncTaskService;
	
	Event<StateChangedEventArgs> stateChangedEvent = new Event<StateChangedEventArgs>();

	
	public HttpSession(HttpClient httpClient, HttpRequestBase request){
		this(httpClient, request, null, null);
	}
	public HttpSession(HttpClient httpClient, HttpRequestBase request, IUITaskService uiTaskService, IAsyncTaskService asyncTaskService) {
		this.url = request.getURI().toString();
		this.httpClient = httpClient;
		this.request = request;
		this.uiTaskService = uiTaskService;
		this.asyncTaskService = asyncTaskService;
	}
	
	public HttpSession(HttpClient httpClient, String url, File file){
		this(httpClient, url, file, null, null);
	}
	public HttpSession(HttpClient httpClient, String url, File file, IUITaskService uiTaskService, IAsyncTaskService asyncTaskService) {
		Logger.check(file != null, "The file parameter must not be null!");
		this.httpClient = httpClient;
		this.url = getNormalizedUrl(url);
		this.file = file;			
		this.fileData = new File(file.getPath() + ".data");
		this.fileInfo = new File(file.getPath() + ".info");
		this.uiTaskService = uiTaskService;
		this.asyncTaskService = asyncTaskService;

		DownloadInfo di = getDownloadInfo(file);
		if (di != null){
			this.downloadedLength = di.getDownloadedLength();
			this.contentLength = di.getContentLength();		
			state = STATE_FINISHED;
		}
		downloadMode = true;		
	}
	public static String getNormalizedUrl(String url){
		return url.contains("://") ? url : "http://" + url;
	}
	public static void deleteDownloadFile(File file){		
		File fileData = new File(file.getPath() + ".data");
		File fileInfo = new File(file.getPath() + ".info");
		if (fileInfo.exists()) fileInfo.delete();
		if (fileData.exists()) fileData.delete();
		if (file.exists()) file.delete();
	}
	public static DownloadInfo getDownloadInfo(File file){
		if (file.exists()){
			return new DownloadInfo(file.length(), file.length());
		}
		DownloadInfo di = null;
		File fileData = new File(file.getPath() + ".data");
		File fileInfo = new File(file.getPath() + ".info");
		if (fileData.exists() && fileInfo.exists()){
			long downloadedLength = fileData.length();
			long contentLength = -1;
			DataInputStream din;
			if (fileInfo.length() == 0)
				return new DownloadInfo(0, 0);
			try {
				din = new DataInputStream(new FileInputStream(
						fileInfo));

				int headerCount = din.readInt();
				for (int i = 0; i < headerCount; i++) {
					String key = din.readUTF();				
					int count = din.readInt();
					List<String> vals = new ArrayList<String>();
					for (int j = 0; j < count; j++) {
						String val = din.readUTF();
						vals.add(val);
					}		
					if ("Content-Length".equals(key)){
						if (contentLength == -1)
							contentLength = Convert.toInteger(vals.get(0));						
					} else if ("Content-Range".equals(key)){
						int pos = vals.get(0).indexOf('/');
						contentLength = Convert.toInteger(vals.get(0).substring(pos + 1));
					}
				}
				din.close();	
				di = new DownloadInfo(downloadedLength, contentLength);
			} catch (Exception e) {
				Logger.error(e);
			}
		}
		return di;
	}
	Map<String, List<String>> headers = new TreeMap<String, List<String>>();

	void readHeaders() {
		try {
			if (!fileInfo.exists() || fileInfo.length() == 0)
				return;
			DataInputStream din = new DataInputStream(new FileInputStream(
					fileInfo));
			headers = new TreeMap<String, List<String>>();
			int headerCount = din.readInt();
			for (int i = 0; i < headerCount; i++) {
				String key = din.readUTF();
				int count = din.readInt();
				List<String> vals = new ArrayList<String>();
				for (int j = 0; j < count; j++) {
					String val = din.readUTF();
					vals.add(val);
				}
				headers.put(key, vals);
			}
			din.close();
		} catch (IOException e) {
			Logger.error(e);
		}
	}

	void writeHeaders() {
		try {
			DataOutputStream dout = new DataOutputStream(new FileOutputStream(
					fileInfo));
			int headerCount = headers.size();
			dout.writeInt(headerCount);
			for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
				String key = entry.getKey();
				List<String> values = entry.getValue();
				dout.writeUTF(key);
				dout.writeInt(values.size());
				for (String value : values) {
					dout.writeUTF(value);
				}
			}
			dout.close();
		} catch (IOException e) {
			Logger.error(e);
		}
	}

	public String getHeader(String name) {
		if (!headers.containsKey(name))
			return null;
		return headers.get(name).get(0);
	}

	public String getUrl() {
		return url;
	}

	public long getDownloadedLength() {
		return downloadedLength;
	}

	public long getContentLength() {
		return contentLength;
	}
	public int getPercent(){
		return (int)(contentLength > 0 ? downloadedLength * 100 / contentLength : 0);
	}
	public Throwable getError() {
		return error;
	}
	public String getResultAsString(String encoding){
		try {
			if (downloadMode) {
				if (file != null)
					return FileHelper.readText(file, encoding);

			} else {
				if (result != null)
					return new String(result, encoding == null ? "UTF-8" : encoding);
			}
		} catch (Exception e) {
			Logger.error(e);
		}
		return null;
	}
	public InputStream getResult(){
		if (downloadMode){
			try {
				if (file != null)
					return new FileInputStream(file);
			} catch (FileNotFoundException e) {
				Logger.error(e);
			}
		} else {
			if (result != null)
				return new ByteArrayInputStream(result);
		}
		return null;
	}
	public File getFile() {
		return file;
	}

	public Event<StateChangedEventArgs> getStateChangedEvent() {
		return stateChangedEvent;
	}

	public int getState() {
		return state;
	}

	String getStateName(int state){
		switch(state){
		case STATE_WAITING : return "STATE_WAITING";
		case STATE_STARTED : return "STATE_STARTED";
		case STATE_HEADER_RECEIVED : return "STATE_HEADER_RECEIVED";
		case STATE_CONTENT_RECEIVING : return "STATE_CONTENT_RECEIVING";
		case STATE_CONTENT_RECEIVED : return "STATE_CONTENT_RECEIVED";
		case STATE_ERROR : return "STATE_ERROR";
		case STATE_FINISHED : return "STATE_FINISHED";
		}
		return "UNKNOWN";
	}
	public boolean isFinished(){
		return state == STATE_FINISHED;
	}
	public void setState(final int state) {
		if (this.state == STATE_FINISHED && state > STATE_STARTED)
			return;
		
		this.state = state;
		if (state == STATE_CONTENT_RECEIVING){
			Logger.debug(url + "(" + getStateName(state) + ") : " + downloadedLength + "/" + contentLength);
		}else{
			Logger.debug(url + "(" + getStateName(state) + ")");
		}
		if (state == STATE_FINISHED)
			this.request = null;
		Runnable task = new Runnable() {
			@Override
			public void run() {
				StateChangedEventArgs args = new StateChangedEventArgs(state, HttpSession.this);
				stateChangedEvent.fireEvent(HttpSession.this, args);
			}
		};
		if (uiTaskService != null)
			uiTaskService.run(task);
		else
			task.run();
	}

	public boolean isDownloaded() {
		return downloadedLength == contentLength;
	}

	void resetFromHeaders() {
		String val = getHeader("Content-Length");
		contentLength = -1;
		if (val != null)
			contentLength = Convert.toInteger(val);
		val = getHeader("Content-Range");
		if (val != null) {
			int pos = val.indexOf('/');
			contentLength = Convert.toInteger(val.substring(pos + 1));
		}
	}
	public boolean isChunked(){
		String val = getHeader("Transfer-Encoding");
		return "chunked".equals(val);
	}
	public void start() {
		if (state != STATE_WAITING && state != STATE_FINISHED){
			Logger.warn(url + " is executing.");
			return;
		}
		error = null;
		downloadedLength = 0;
		contentLength = -1;		
		if (downloadMode){
			try{
				request = new HttpGet(url);
			}catch(Exception e){
				error = e;
				setState(STATE_ERROR);	
				setState(STATE_FINISHED);				
				return;
			}
			if (file.exists()) file.delete();
			if (fileData.exists() && fileInfo.exists()) {
				request.setHeader("Range", "bytes=" + fileData.length() + "-");
				downloadedLength = fileData.length();
				readHeaders();
				resetFromHeaders();
			} else {
				try {
					if (fileData.exists()) fileData.delete();
					fileData.createNewFile();
					if (fileInfo.exists()) fileInfo.delete();
					fileInfo.createNewFile();
				} catch (IOException e) {
					Logger.error(e);
				}
			}
			request.setHeader("Accept-Encoding", "gzip");	
		} 
		Logger.debug("Request headers of " + url + " : ");
		if (request != null) {
			for(Header header : request.getAllHeaders()){
				Logger.debug(header.getName() + " = " + header.getValue());
			}
		}
		setState(STATE_STARTED);
		Runnable task = new Runnable(){

			@Override
			public void run() {
				try {
					httpClient.execute(request, new ResponseHandler<Object>() {

						@Override
						public Object handleResponse(HttpResponse response) {
							statusCode = response.getStatusLine().getStatusCode();
							Logger.debug("Response headers of " + url + "[" + statusCode + "] : ");
							for(Header header : response.getAllHeaders()){
								Logger.debug(header.getName() + " = " + header.getValue());
							}
							
							headers = new TreeMap<String, List<String>>();
							for (Header h : response.getAllHeaders()) {
								String key = h.getName();
								List<String> vals = null;
								if (headers.containsKey(key))
									vals = headers.get(key);
								else {
									vals = new ArrayList<String>();
									headers.put(key, vals);
								}
								vals.add(h.getValue());
							}
							if (downloadMode){
								writeHeaders();
							}
							resetFromHeaders();
							setState(STATE_HEADER_RECEIVED);
							HttpEntity entity = response.getEntity();
							if (entity != null) {
								try {
									InputStream instream = entity.getContent();
									Header contentEncoding = response
											.getFirstHeader("Content-Encoding");
									if (contentEncoding != null
											&& contentEncoding.getValue()
													.equalsIgnoreCase("gzip")) {
										instream = new GZIPInputStream(instream);
									}
									InputStream input = new BufferedInputStream(
											instream);
									OutputStream output = downloadMode ? new FileOutputStream(fileData, fileData.exists()) : new ByteArrayOutputStream();

									byte data[] = new byte[1024 * 10];
									int count;
									int percent = -1;
									while ((count = input.read(data)) != -1) {
										downloadedLength += count;
										if (contentLength > 0 && downloadedLength * 100 / contentLength != percent) {
											percent = (int) (downloadedLength * 100 / contentLength);
											setState(STATE_CONTENT_RECEIVING);
										}
										output.write(data, 0, count);
									}

									output.flush();
									if (!downloadMode) result = ((ByteArrayOutputStream)output).toByteArray();
									output.close();
									instream.close();

									if (downloadMode){
										if (isDownloaded() || isChunked()){
											if (file.exists())
												file.delete();
											FileHelper.copy(fileData, file);
											fileData.delete();
											fileInfo.delete();
											setState(STATE_CONTENT_RECEIVED);	
										}
									} else {
										setState(STATE_CONTENT_RECEIVED);														
									}								
								} catch (Exception e) {
									error = e;
									Logger.error(e);
									setState(STATE_ERROR);
								} finally {
									setState(STATE_FINISHED);
								}
							}
							return null;
						}
					});
				} catch (Exception e) {
					error = e;
					Logger.error(e);
					setState(STATE_ERROR);
					setState(STATE_FINISHED);		
				}
			};
		};
		if (asyncTaskService != null)
			asyncTaskService.run(task);
		else
			task.run();
	}

	public void stop() {
		if (request != null){
			request.abort();
			request = null; 
		}
	}
	public int getStatusCode() {
		return statusCode;
	}

}
