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
package com.momock.service;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.zip.GZIPInputStream;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;

import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.os.Build;

import com.momock.app.App;
import com.momock.util.Convert;
import com.momock.util.Logger;

public class Downloader implements IDownloader {

	AndroidHttpClient httpClient;
	int activeTaskCount = 0;

	class HttpRequestTask extends AsyncTask<Void, Void, Void> {
		HttpRequestBase request;
		HttpSession session = null;
		public HttpRequestTask(HttpSession session){
			this.session = session;
		}
		@Override
		protected Void doInBackground(Void... params) {

			try {
				request = new HttpGet(session.getUrl());
				request.setHeader("Accept", "application/json");
				request.setHeader("Accept-Encoding", "gzip");
				ICacheService cacheService = App.get().getService(ICacheService.class);
				File file = cacheService.getCacheOf("downloader", session.getUrl());
				session.setFile(file);
				if (file.exists()){
					request.setHeader("Range", "bytes=" + file.length() + "-");
				}

				httpClient.execute(request, new ResponseHandler<Object>() {

					@Override
					public Object handleResponse(HttpResponse response) {
						Header[] hs;
						hs = response.getHeaders("Content-Length");
						int contentLength = -1;
						if (hs != null)
							contentLength = Convert.toInteger(hs[0].getValue());
						hs = response.getHeaders("Content-Range");
						if (hs != null){
							String cr = hs[0].getValue();
							int pos = cr.indexOf('/');
							contentLength = Convert.toInteger(cr.substring(pos + 1));
						}
						session.setContentLength(contentLength);
						App.get().execute(new Runnable() {

							@Override
							public void run() {
								if (session.getCallback() != null)
									session.getCallback().onHeaderReceived(
											session);
							}

						});
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
								OutputStream output = new FileOutputStream(session.getFile(), session.getFile().exists());

								byte data[] = new byte[1024 * 10];
								int received = 0;
								int count;
								int percent = -1;
								while ((count = input.read(data)) != -1) {
									received += count;
									session.setDownloadedLength(received);
									if (received * 100 / contentLength != percent) {
										percent = received * 100
												/ contentLength;
										publishProgress();
									}
									output.write(data, 0, count);
								}

								output.flush();
								output.close();
								instream.close();
							} catch (Exception e) {
								Logger.error(e.getMessage());
							}
						}
						return null;
					}
				});
			} catch (Exception e) {
				session.setError(e);
				Logger.error(session.getUrl() + " : " + e.getMessage());
				App.get().execute(new Runnable() {

					@Override
					public void run() {
						if (session.getCallback() != null)
							session.getCallback().onError(session);
					}

				});
			}
			return null;
		}

		@Override
		protected void onProgressUpdate(Void... params) {
			if (session.getCallback() != null)
				session.getCallback().onContentReceiving(session);
			if (session.getDownloadedLength() == session.getContentLength()) {
				if (session.getCallback() != null)
					session.getCallback().onContentReceived(session);
			}
		}

		@Override
		protected void onPostExecute(Void result) {
			if (session.getCallback() != null)
				session.getCallback().onFinish(session);
			removeSession(session.getUrl());
			activeTaskCount--;
			Logger.debug("Finish download " + session.getUrl());
		}

		@Override
		protected void onPreExecute() {
			Logger.debug("Start download " + session.getUrl());
			activeTaskCount++;
			if (session.getCallback() != null)
				session.getCallback().onStart(session);
		}

		public final android.os.AsyncTask<Void, Void, Void> executeOnThreadPool(
				Void... params) {
			if (Build.VERSION.SDK_INT < 4) {
				return execute(params);
			} else if (Build.VERSION.SDK_INT < 11) {
				return execute(params);
			} else {
				try {
					Method method = android.os.AsyncTask.class
							.getMethod("executeOnExecutor", Executor.class,
									Object[].class);
					Field field = android.os.AsyncTask.class
							.getField("THREAD_POOL_EXECUTOR");
					Object executor = field.get(null);
					method.invoke(this, executor, params);
				} catch (NoSuchMethodException e) {
					throw new RuntimeException(
							"Unexpected NoSuchMethodException", e);
				} catch (NoSuchFieldException e) {
					throw new RuntimeException(
							"Unexpected NoSuchFieldException", e);
				} catch (IllegalAccessException e) {
					throw new RuntimeException(
							"Unexpected IllegalAccessException", e);
				} catch (InvocationTargetException e) {
					throw new RuntimeException(
							"Unexpected InvocationTargetException", e);
				}
				return this;
			}
		}

	}

	@Override
	public void start() {
		httpClient = AndroidHttpClient.newInstance("Android");
	}

	@Override
	public void stop() {
		httpClient.close();
	}

	protected Map<String, HttpSession> sessions = new HashMap<String, HttpSession>();
	protected LinkedList<HttpSession> queue = new LinkedList<HttpSession>();

	@Override
	public HttpSession getSession(String url) {
		return sessions.get(url);
	}

	Iterator<HttpSession> getSessionFromQueue(String url) {
		Iterator<HttpSession> it = queue.iterator();
		while (it.hasNext()) {
			HttpSession session = it.next();
			if (url.equals(session.getUrl())) {
				return it;
			}
		}
		return null;
	}

	static final int MAX_TASK_COUNT = 5;

	void flushQueue() {
		while (activeTaskCount < MAX_TASK_COUNT && !queue.isEmpty()) {
			new HttpRequestTask(queue.poll()).executeOnThreadPool();
		}
	}

	@Override
	public synchronized void removeSession(String url) {
		sessions.remove(url);
		Iterator<HttpSession> it = getSessionFromQueue(url);
		if (it != null) it.remove();
		flushQueue();
	}
	@Override
	public HttpSession addSession(String url, HttpSession.Callback callback){
		return addSession(url, callback, false);
	}
	@Override
	public synchronized HttpSession addSession(String url,
			HttpSession.Callback callback, boolean highPriority) {
		HttpSession session = getSession(url);
		if (session != null) {
			Iterator<HttpSession> it = getSessionFromQueue(url);
			if (it != null) {
				if (highPriority) {
					it.remove();
					queue.add(0, session);
				}
				if (callback != null)
					session.setCallback(callback);
			}
			return session;
		} else {
			session = new HttpSession(url);
			session.setCallback(callback);
			sessions.put(url, session);
			if (highPriority)
				queue.add(0, session);
			else
				queue.add(session);
		}
		flushQueue();
		return session;
	}

}
