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

import com.momock.event.EventArgs;
import com.momock.event.IEventHandler;

public interface ICrashReportService extends IService{
	public static class CrashEventArgs extends EventArgs{
		Thread thread;
		Throwable error;
		public CrashEventArgs(Thread thread, Throwable error){
			this.thread = thread;
			this.error = error;
		}
		public Thread getThread() {
			return thread;
		}
		public Throwable getError() {
			return error;
		}
	}
	void onCrash(Thread thread, Throwable error);
	void addCrashHandler(IEventHandler<CrashEventArgs> handler);
}
