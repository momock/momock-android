package com.momock.net;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
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

import com.momock.app.App;
import com.momock.event.Event;
import com.momock.event.EventArgs;
import com.momock.service.ICacheService;
import com.momock.util.Convert;
import com.momock.util.Logger;

public class HttpSession {
	public static final int STATE_CREATED = 0;
	public static final int STATE_STARTED = 1;
	public static final int STATE_HEADER_RECEIVED = 2;
	public static final int STATE_CONTENT_RECEIVING = 3;
	public static final int STATE_CONTENT_RECEIVED = 4;
	public static final int STATE_ERROR = 5;
	public static final int STATE_FINISHED = 6;

	public static class StateChangedEventArgs extends EventArgs {
		int state;

		public StateChangedEventArgs(int state) {
			this.state = state;
		}

		public int getState() {
			return state;
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
	int state = STATE_CREATED;
	HttpRequestBase request = null;
	Event<StateChangedEventArgs> stateChangedEvent = new Event<StateChangedEventArgs>();

	public HttpSession(HttpClient httpClient, String url) {
		this(httpClient, url, null);
	}

	public HttpSession(HttpClient httpClient, String url, File file) {
		ICacheService cacheService = App.get().getService(ICacheService.class);
		Logger.check(cacheService != null, "ICacheService has not been added!");
		this.httpClient = httpClient;
		this.url = url;
		if (file != null)
			this.file = file;
		else
			this.file = cacheService.getCacheOf(this.getClass().getName(), url);
		this.fileData = new File(file.getPath() + ".data");
		this.fileInfo = new File(file.getPath() + ".info");
		if (fileData.exists() && fileInfo.exists()) {
			downloadedLength = fileData.length();
			readHeaders();
			resetFromHeaders();
		}
	}

	Map<String, List<String>> headers = new TreeMap<String, List<String>>();

	void readHeaders() {
		try {
			if (!fileInfo.exists())
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
			Logger.error(e.getMessage());
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
			Logger.error(e.getMessage());
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
		case STATE_CREATED : return "STATE_CREATED";
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
	protected void setState(final int state) {
		this.state = state;
		if (state == STATE_CONTENT_RECEIVING){
			Logger.debug(url + "(" + getStateName(state) + ") : " + downloadedLength + "/" + contentLength);
		}else{
			Logger.debug(url + "(" + getStateName(state) + ")");
		}
		if (state == STATE_FINISHED)
			this.request = null;
		App.get().execute(new Runnable() {
			@Override
			public void run() {
				StateChangedEventArgs args = new StateChangedEventArgs(state);
				stateChangedEvent.fireEvent(HttpSession.this, args);
			}
		});
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

	public void start() {
		if (request != null){
			Logger.warn(url + " has already been started.");
			return;
		}
		request = new HttpGet(url);
		request.setHeader("Accept", "application/json");
		request.setHeader("Accept-Encoding", "gzip");
		if (file.exists()) {
			request.setHeader("Range", "bytes=" + file.length() + "-");
		}
		error = null;
		setState(STATE_STARTED);
		try {
			httpClient.execute(request, new ResponseHandler<Object>() {

				@Override
				public Object handleResponse(HttpResponse response) {

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
					writeHeaders();
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
							OutputStream output = new FileOutputStream(
									fileData, fileData.exists());

							byte data[] = new byte[1024 * 10];
							int count;
							int percent = -1;
							while ((count = input.read(data)) != -1) {
								downloadedLength += count;
								if (downloadedLength * 100 / contentLength != percent) {
									percent = (int) (downloadedLength * 100 / contentLength);
									setState(STATE_CONTENT_RECEIVING);
								}
								output.write(data, 0, count);
							}

							output.flush();
							output.close();
							instream.close();
							setState(STATE_CONTENT_RECEIVED);
						} catch (Exception e) {
							error = e;
							Logger.error(e.getMessage());
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
			Logger.error(e.getMessage());
			setState(STATE_ERROR);
		} finally {
			setState(STATE_FINISHED);			
		}
	}

	public void stop() {
		if (request != null){
			request.abort();
			request = null;
		}
	}
}
