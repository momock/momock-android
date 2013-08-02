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

import javax.inject.Inject;

import android.content.Context;

import com.momock.app.IApplication;
import com.momock.util.Logger;

public class EmailCrashReportService extends CrashReportService {
	@Inject
	IEmailService emailService = null;
	@Inject 
	Context context;
	@Inject 
	IApplication app;
	String sender;
	String[] receivers;
	public EmailCrashReportService(String sender, String[] receivers){
		this.sender = sender;
		this.receivers = receivers;
	}
	
	@Override
	public void onCrash(Thread thread, Throwable error) {
		if (emailService != null){
			String msg = EmailDeviceInfoHelper.getFullMessage(context, Logger.getStackTrace(error));
			emailService.send(sender,
					receivers, 
					"CRASH > " + context.getPackageName() + " v" + app.getVersion() + " : " + error + " in " + thread,
					msg, null);
		}
	}

	@Override
	public Class<?>[] getDependencyServices() {
		return new Class<?>[] {IEmailService.class};
	}

}
