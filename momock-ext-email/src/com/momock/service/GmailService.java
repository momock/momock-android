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

import java.io.File;

import javax.inject.Inject;

import com.momock.email.GmailClient;
import com.momock.util.Logger;

public class GmailService implements IEmailService {
	@Inject
	IAsyncTaskService asyncTaskService;
	String username;
	String password;
	static final String ERROR_SEPERATOR = "\r\n+++++++++++++++++++++++++++++++++++++++++++++++++\r\n";

	public GmailService(String username, String password) {
		this.username = username;
		this.password = password;
	}

	@Override
	public Class<?>[] getDependencyServices() {
		return new Class<?>[]{ IAsyncTaskService.class };
	}

	@Override
	public void start() {

	}

	@Override
	public void stop() {

	}
	
	@Override
	public void send(final String sender, final String[] receivers, final String subject, final String body, final File[] files) {
		asyncTaskService.run(new Runnable(){

			@Override
			public void run() {
				GmailClient client = new GmailClient(username, password);
				client.setSender(sender);
				client.setReceivers(receivers);
				client.setSubject(subject);
				client.setBody(body);
				try {
					if (files != null){
						for(File f : files){
							if (f.exists())
								client.addAttachment(f.getAbsolutePath());					
						}
					}
					client.send();
				} catch (Exception e) {
					try {
						Thread.sleep(10 * 1000);
					} catch (InterruptedException ie) {
					}
					send(sender, receivers, subject, body, files);
					Logger.error(e);
				} 
			}
			
		});
	}

	@Override
	public boolean canStop() {
		return true;
	}
}
