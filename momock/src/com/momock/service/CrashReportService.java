/*******************************************************************************
 * Copyright 2013 momock.com
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

import com.momock.event.Event;
import com.momock.event.IEvent;
import com.momock.event.IEventHandler;
import com.momock.util.Logger;

public class CrashReportService implements ICrashReportService {
	protected IEvent<CrashEventArgs> event = new Event<CrashEventArgs>();
	
	protected Thread.UncaughtExceptionHandler defaultExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
	public CrashReportService(){
		Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
			@Override
			public void uncaughtException(Thread thread, Throwable error) {
				CrashEventArgs args = new CrashEventArgs(thread, error);
				event.fireEvent(CrashReportService.this, args);
				onCrash(thread, error);
				defaultExceptionHandler.uncaughtException(thread, error);
			}
		});
	}
	@Override
	public Class<?>[] getDependencyServices() {
		return null;
	}

	@Override
	public void start() {

	}

	@Override
	public void stop() {

	}
	@Override
	public void addCrashHandler(IEventHandler<CrashEventArgs> handler) {
		event.addEventHandler(handler);
	}
	@Override
	public void onCrash(Thread thread, Throwable error) {
		Logger.error(error);
	}
	@Override
	public boolean canStop() {
		return true;
	}

}
