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

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import android.net.http.AndroidHttpClient;

import com.momock.net.HttpSession;

public class Downloader implements IDownloader {
	Timer timer = null;
	String userAgent; 
	HttpSession executingSessions[];
	AndroidHttpClient httpClient;

	public Downloader(){
		this(10, "Android");
	}
	public Downloader(int maxTaskCount, String userAgent){
		executingSessions = new HttpSession[maxTaskCount];
		this.userAgent = userAgent;
	}
	@Override
	public void start() {		
		httpClient = AndroidHttpClient.newInstance(userAgent);
	}
	void startTimer(){
		if (timer != null) return;
		timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask(){

			@Override
			public void run() {
				processQueue();
			}
			
		}, 1000, 1000);
	}
	void stopTimer(){
		timer.cancel();
		timer = null;
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

	void processQueue() {
		if (queue.isEmpty())
			stopTimer();
		else{
			for(int i = 0; i < executingSessions.length && !queue.isEmpty(); i++){
				HttpSession session = executingSessions[i];
				if (session != null){
					if (session.isFinished())
						session = null;
				}
				if (session == null){
					session = queue.poll();
					executingSessions[i] = session;
					session.start();
				}					
			}
		}
	}

	void resetTimer(){
		if (queue.isEmpty())
			stopTimer();
		else if (timer == null){
			startTimer();
		}
	}
	@Override
	public synchronized void removeSession(String url) {
		sessions.remove(url);
		Iterator<HttpSession> it = getSessionFromQueue(url);
		if (it != null) it.remove();
		resetTimer();
	}
	@Override
	public HttpSession addSession(String url){
		return addSession(url, false);
	}
	@Override
	public HttpSession addSession(String url, boolean highPriority) {
		return addSession(url, null, highPriority);
	}

	@Override
	public HttpSession addSession(String url, File file) {
		return addSession(url, file, false);
	}

	@Override
	public synchronized HttpSession addSession(String url, File file, boolean highPriority) {
		HttpSession session = getSession(url);
		if (session != null) {
			Iterator<HttpSession> it = getSessionFromQueue(url);
			if (it != null) {
				if (highPriority) {
					it.remove();
					queue.add(0, session);
				}
			}
			return session;
		} else {
			session = new HttpSession(httpClient, url, file);
			sessions.put(url, session);
			if (highPriority)
				queue.add(0, session);
			else
				queue.add(session);
		}
		resetTimer();
		return session;
	}

}
