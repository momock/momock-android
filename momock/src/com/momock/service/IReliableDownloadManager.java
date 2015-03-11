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

import com.momock.event.Event;
import com.momock.event.EventArgs;

public interface IReliableDownloadManager {

	public static final int STATE_STOPPED = -1;
	public static final int STATE_WAITING = 0;
	public static final int STATE_DOWNLOADING = 1;
	public static final int STATE_DOWNLOADED = 2;

	public interface IDownloadItem {
		String getUrl();
		File getFile();
		boolean isWifiOnly();
		void setWifiOnly(boolean wo);
		void setStopped(boolean stop);
		int getState();
		long getDownloadedLength();
		long getContentLength();
		boolean isFinished();
		int getPercent();
		void start();
		void stop();
		void pause();
		void remove(boolean deleteDownloadedFile);
		Date getCreatedAt();
		Date getFinishedAt();
		long getAutoDeleteAfter();
		void setAutoDeleteAfter(long secs);
		boolean isAutoInstall();
		void setAutoInstall(boolean install);	
		boolean isSilentlyInstall();
		void setSilentlyInstall(boolean install);		
		Event<EventArgs> getNotificationEvent();
	}

	IDownloadItem download(String url, boolean wifiOnly);
	IDownloadItem get(String url);
	IDownloadItem[] getAll();
}
