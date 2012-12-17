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
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.Executor;
import java.util.zip.GZIPInputStream;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;

import android.os.AsyncTask;
import android.os.Build;

import com.momock.app.App;
import com.momock.event.Event;
import com.momock.event.EventArgs;
import com.momock.service.ICacheService;
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
				StateChangedEventArgs args = new StateChangedEventArgs(state, HttpSession.this);
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

	public void resume(){
		if (request == null){
			this.state = STATE_WAITING;
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
		if (fileData.exists()) {
			request.setHeader("Range", "bytes=" + fileData.length() + "-");
		}
		error = null;
		downloadedLength = 0;
		contentLength = -1;
		if (fileData.exists() && fileInfo.exists()) {
			downloadedLength = fileData.length();
			readHeaders();
			resetFromHeaders();
		}		
		setState(STATE_STARTED);
		App.get().execute(new Runnable(){

			@Override
			public void run() {

				AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>(){

					@Override
					protected Void doInBackground(Void... params) {
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
												if (contentLength > 0 && downloadedLength * 100 / contentLength != percent) {
													percent = (int) (downloadedLength * 100 / contentLength);
													setState(STATE_CONTENT_RECEIVING);
												}
												output.write(data, 0, count);
											}

											output.flush();
											output.close();
											instream.close();
											if (contentLength == -1)
												contentLength = downloadedLength;
											if (isDownloaded()){
												if (file.exists())
													file.delete();
												FileHelper.copyFile(fileData, file);
												fileData.delete();
												fileInfo.delete();
												setState(STATE_CONTENT_RECEIVED);
											} else {
												throw new RuntimeException("Why ?");
											}
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
							setState(STATE_FINISHED);		
						}
						return null;
					}					

				};

		        if (Build.VERSION.SDK_INT < 4) {
		        	task.execute();
		        } else if (Build.VERSION.SDK_INT < 11) {
		        	task.execute();
		        } else {
		            try {
		                Method method = android.os.AsyncTask.class.getMethod("executeOnExecutor",
		                        Executor.class, Object[].class);
		                Field field = android.os.AsyncTask.class.getField("THREAD_POOL_EXECUTOR");
		                Object executor = field.get(null);
		                method.invoke(task, executor);
		            } catch (NoSuchMethodException e) {
		                throw new RuntimeException("Unexpected NoSuchMethodException", e);
		            } catch (NoSuchFieldException e) {
		                throw new RuntimeException("Unexpected NoSuchFieldException", e);
		            } catch (IllegalAccessException e) {
		                throw new RuntimeException("Unexpected IllegalAccessException", e);
		            } catch (InvocationTargetException e) {
		                throw new RuntimeException("Unexpected InvocationTargetException", e);
		            }
		        }
			}
			
		});
	}

	public void stop() {
		if (request != null){
			request.abort();
			request = null; 
		}
	}

}
