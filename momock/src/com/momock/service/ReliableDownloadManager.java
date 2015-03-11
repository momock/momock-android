/*******************************************************************************
 * Copyright 2015 momock.com
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
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;

import com.momock.event.Event;
import com.momock.event.EventArgs;
import com.momock.event.IEventHandler;
import com.momock.http.HttpSession;
import com.momock.util.FileHelper;
import com.momock.util.InstallHelper;
import com.momock.util.JsonDatabase;
import com.momock.util.Logger;

public class ReliableDownloadManager implements IReliableDownloadManager {
	class DownloadItem implements IDownloadItem{
		public static final String PROP_URL = "url";
		public static final String PROP_CREATED_AT = "created_at";
		public static final String PROP_FINISHED_AT = "finished_at";
		public static final String PROP_WIFO_ONLY = "wo";
		public static final String PROP_AUTO_INSTALL = "auto_install";
		public static final String PROP_SILENTLY_INSTALL = "silently_install";
		public static final String PROP_AUTO_DELETE_AFTER = "auto_delete_after";
		public static final String PROP_STOPPED = "stopped";

		String url;
		boolean stopped = false;
		boolean wifiOnly;
		HttpSession session;
		Date createdAt;
		JSONObject joData;
		Event<EventArgs> event = new Event<EventArgs>();
		
		@Override
		public Event<EventArgs> getNotificationEvent(){
			return event;
		}
		public HttpSession getHttpSession(){
			if (session == null){
				session = httpService.download(url, new File(downloadDir, FileHelper.getFilenameOf(url)));
				session.getStateChangedEvent().addEventHandler(new IEventHandler<HttpSession.StateChangedEventArgs>(){

					@Override
					public void process(Object sender, HttpSession.StateChangedEventArgs args) {
						if (args.getState() == HttpSession.STATE_FINISHED && session.isDownloaded() && getFile() != null){
							onDownloadFinished();
						} 
						if (args.getState() == HttpSession.STATE_ERROR && !isStopped()){
							Timer timer = new Timer();
							timer.schedule(new TimerTask(){

								@Override
								public void run() {
									Logger.debug("Retry to download file : " + getUrl());
									start();
								}
								
							}, 5 * 1000);
						}
						getNotificationEvent().fireEvent(DownloadItem.this, null);
					}
					
				});
			}
			return session;
		}
		public DownloadItem(JsonDatabase.Document doc){
			joData = doc.getData();
			try {
				url = joData.getString(PROP_URL);
				createdAt = new Date(joData.getLong(PROP_CREATED_AT));
				wifiOnly = joData.getBoolean(PROP_WIFO_ONLY);
				stopped = joData.getBoolean(PROP_STOPPED);
			} catch (JSONException e) {
				Logger.error(e);
			}
			if (stopped != true){
				start();
			}
		}
		public DownloadItem(String url, boolean wifiOnly){
			this.url = url;
			this.wifiOnly = wifiOnly;
			joData = new JSONObject();
			try {
				joData.put(PROP_URL, url);
				createdAt = new Date();
				joData.put(PROP_CREATED_AT, createdAt.getTime());
				joData.put(PROP_WIFO_ONLY, wifiOnly);
				joData.put(PROP_STOPPED, false);
			} catch (JSONException e) {
				Logger.error(e);
			}
			coldm.set(url, joData);
			start();
		}
		public void onDownloadFinished(){
			try {
				joData.put(PROP_FINISHED_AT, new Date().getTime());
				coldm.set(url, joData);
			} catch (JSONException e) {
				Logger.error(e);
			}
			if (getFile().exists()){
				if (isSilentlyInstall()){
					Logger.debug("Silently install : " + url + " @ " + getFile().getAbsolutePath());
					if (!InstallHelper.directInstall(context, getFile())){

						Timer timer = new Timer();
						timer.schedule(new TimerTask(){

							@Override
							public void run() {
								Logger.debug("Silently install fallback to auto install");
								InstallHelper.install(context, getFile());
							}
							
						}, 1000);
					}
				} else if (isAutoInstall()){
					Logger.debug("Auto install : " + url + " @ " + getFile().getAbsolutePath());
					InstallHelper.install(context, getFile());			
				}
			}
			long secs = getAutoDeleteAfter();
			if (secs > 0){
				Timer timer = new Timer();
				timer.schedule(new TimerTask(){

					@Override
					public void run() {
						Logger.debug("Auto delete downloaded file : " + getUrl() + "(" + getFinishedAt() + "," + getAutoDeleteAfter() + "s)");
						remove(true);
					}
					
				}, secs * 1000);
			}
		}
		@Override
		public String getUrl() {
			return url;
		}

		@Override
		public File getFile() {
			return getHttpSession().getFile();
		}

		@Override
		public boolean isWifiOnly() {
			return wifiOnly;
		}

		@Override
		public void setWifiOnly(boolean wo) {
			wifiOnly = wo;
			try {
				joData.put(PROP_WIFO_ONLY, wo);
				coldm.set(url, joData);
			} catch (JSONException e) {
				Logger.error(e);
			}
		}
		
		@Override
		public void setStopped(boolean stop){
			stopped = stop;
			try {
				joData.put(PROP_STOPPED, stop);
				coldm.set(url, joData);
			} catch (JSONException e) {
				Logger.error(e);
			}
		}

		@Override
		public long getDownloadedLength() {
			return getHttpSession().getDownloadedLength();
		}

		@Override
		public long getContentLength() {
			return getHttpSession().getContentLength();
		}

		@Override
		public boolean isFinished() {
			return getFile().exists();
		}

		@Override
		public int getPercent() {
			return getHttpSession().getPercent();
		}

		@Override
		public void start() {
			if (isFinished()) {
				return;
			}
			if (hasConnectivity && (!wifiOnly || wifiOnly && wifi)){
				Logger.debug("Download item starts : " + url);
				stopped = false;
				setStopped(false);
				getHttpSession().start();	
			}
		}

		@Override
		public void stop() {
			Logger.debug("Download item stops : " + url);
			stopped = true;
			getHttpSession().stop();
		}

		@Override
		public void remove(boolean deleteDownloadedFile) {
			stop();
			if (!getFile().exists() || deleteDownloadedFile)
				HttpSession.deleteDownloadFile(getFile());
			hm.remove(url);
			coldm.set(url, null);
			Logger.debug("Delete downloaded file : " + url);
		}
		
		@Override
		public void pause(){
			Logger.debug("Download item pauses : " + url);
			getHttpSession().stop();
		}

		@Override
		public Date getCreatedAt() {
			return createdAt;
		}

		@Override
		public int getState() {
			if (isFinished())
				return STATE_DOWNLOADED;
			if (stopped) 
				return STATE_STOPPED;
			else {
				switch(getHttpSession().getState()){
				case HttpSession.STATE_WAITING : 
				case HttpSession.STATE_ERROR :
				case HttpSession.STATE_FINISHED : 
					return STATE_WAITING;
				case HttpSession.STATE_STARTED : 
				case HttpSession.STATE_HEADER_RECEIVED : 
				case HttpSession.STATE_CONTENT_RECEIVING : 
				case HttpSession.STATE_CONTENT_RECEIVED : 
					return STATE_DOWNLOADING;
				}
			}
			return STATE_WAITING;
		}
		@Override
		public Date getFinishedAt() {			
			if (joData.has(PROP_FINISHED_AT))
				try {
					return new Date(joData.getLong(PROP_FINISHED_AT));
				} catch (JSONException e) {
					Logger.error(e);
				}
			return null;
		}
		@Override
		public long getAutoDeleteAfter() {
			if (joData.has(PROP_AUTO_DELETE_AFTER))
				try {
					return joData.getLong(PROP_AUTO_DELETE_AFTER);
				} catch (JSONException e) {
					Logger.error(e);
				}
			return -1;
		}
		@Override
		public void setAutoDeleteAfter(long secs) {
			try {
				joData.put(PROP_AUTO_DELETE_AFTER, secs);
				coldm.set(url, joData);
			} catch (JSONException e) {
				Logger.error(e);
			}
			
		}
		@Override
		public boolean isAutoInstall() {
			if (joData.has(PROP_AUTO_INSTALL))
				try {
					return joData.getBoolean(PROP_AUTO_INSTALL);
				} catch (JSONException e) {
					Logger.error(e);
				}
			return false;
		}
		@Override
		public void setAutoInstall(boolean install) {
			try {
				joData.put(PROP_AUTO_INSTALL, install);
				coldm.set(url, joData);
			} catch (JSONException e) {
				Logger.error(e);
			}
			
		}
		public boolean isStopped() {
			return stopped;
		}
		@Override
		public boolean isSilentlyInstall() {
			if (joData.has(PROP_SILENTLY_INSTALL))
				try {
					return joData.getBoolean(PROP_SILENTLY_INSTALL);
				} catch (JSONException e) {
					Logger.error(e);
				}
			return false;
		}
		@Override
		public void setSilentlyInstall(boolean install) {
			try {
				joData.put(PROP_SILENTLY_INSTALL, install);
				coldm.set(url, joData);
			} catch (JSONException e) {
				Logger.error(e);
			}
		}
	}

	BroadcastReceiver connectivityChanged = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			checkNetwork();
		}
	}; 

	boolean isConnected(int type){
		return cm.getNetworkInfo(type) == null ? false : cm.getNetworkInfo(type).isConnectedOrConnecting();
	}
	void checkNetwork(){
		try {
			wifi = isConnected(ConnectivityManager.TYPE_WIFI);
			gprs = isConnected(ConnectivityManager.TYPE_MOBILE);
			hasConnectivity = wifi || gprs;
			Logger.debug("Network status : WIFI = " + wifi + " , GPRS = " + gprs);
			for(String url : hm.keySet()){
				DownloadItem di = (DownloadItem)hm.get(url);
				if (hasConnectivity){
					if (di.stopped != true)
						di.start();
				} else {
					//di.stop();
					di.pause();
				}
			}
		} catch (Exception e) {
			Logger.error(e);
		}
	}
	IHttpService httpService;
	File downloadDir;
	JsonDatabase.Collection coldm;
	ConnectivityManager cm = null;
	boolean hasConnectivity;
	boolean wifi = false;
	boolean gprs = false;
	Context context;
	public ReliableDownloadManager(Context context, IHttpService httpService, JsonDatabase.Collection coldm, File downloadDir){
		this.context = context.getApplicationContext();
		this.httpService = httpService;
		this.downloadDir = downloadDir;		
		setCollection(coldm);
		context.registerReceiver(connectivityChanged, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));	
		cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);  	
		checkNetwork();
	}
	public JsonDatabase.Collection getCollection(){
		return coldm;
	}
	public void setCollection(JsonDatabase.Collection coldm){
		for(String url : hm.keySet()){
			DownloadItem di = (DownloadItem)hm.get(url);
			di.stop();
		}
		hm.clear();
		this.coldm = coldm;
		for(JsonDatabase.Document doc : coldm.list()){
			DownloadItem di = new DownloadItem(doc);
			hm.put(doc.getId(), di);
			if (di.isFinished() && di.getFinishedAt() != null && di.getAutoDeleteAfter() > 0){
				long delAt = di.getFinishedAt().getTime() + di.getAutoDeleteAfter() * 1000;
				if (delAt < new Date().getTime()){
					Logger.debug("Auto delete downloaded file : " + di.getUrl() + "(" + di.getFinishedAt() + "," + di.getAutoDeleteAfter() + "s)");
					di.remove(true);
				}
			}
		}
	}
	ConcurrentHashMap<String, IDownloadItem> hm = new ConcurrentHashMap<String, IDownloadItem>();
	@Override
	public IDownloadItem get(String url) {		
		return hm.get(url);
	}

	@Override
	public IDownloadItem[] getAll() {
		IDownloadItem[] items = new IDownloadItem[hm.size()];
		return hm.values().toArray(items);
	}

	@Override
	public IDownloadItem download(String url, boolean wifiOnly) {
		IDownloadItem di = get(url);
		if (di == null) {
			di = new DownloadItem(url, wifiOnly);	
			hm.put(url, di);
		}
		if (di.isFinished())
			((DownloadItem)di).onDownloadFinished();
		else
			di.start();
		return di;
	}


}
