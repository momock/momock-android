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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.momock.message.IMessageHandler;
import com.momock.message.Message;
import com.momock.util.Logger;

public class MessageService implements IMessageService{
	Map<String, List<IMessageHandler>> allHandlers = new HashMap<String, List<IMessageHandler>>();

	IUITaskService uiTaskService = null;
	public MessageService(){
		
	}
	public MessageService(IUITaskService uiTaskService){
		this.uiTaskService = uiTaskService;
	}
	@Override
	public void addHandler(String topic, IMessageHandler handler) {
		Logger.check(topic != null, "Parameter topic cannot be null!");
		List<IMessageHandler> hs;
		if (allHandlers.containsKey(topic))
			hs = allHandlers.get(topic);
		else {
			hs = new ArrayList<IMessageHandler>();
			allHandlers.put(topic, hs);
		}
		if (!hs.contains(handler))
			hs.add(handler);
	}
	@Override
	public void removeHandler(String topic, IMessageHandler handler) {
		Logger.check(topic != null, "Parameter topic cannot be null!");
		List<IMessageHandler> hs;
		if (allHandlers.containsKey(topic)) {
			hs = allHandlers.get(topic);
			if (hs.contains(handler))
				hs.remove(handler);
		}
	}
	@Override
	public void send(Object sender, String topic){
		send(sender, topic, null);
	}
	@Override
	public void send(Object sender, String topic, Object data){
		Message msg = new Message(topic, data);
		send(sender, msg);
	}
	@Override
	public void send(final Object sender, final Message msg) {
		Runnable task = new Runnable(){

			@Override
			public void run() {
				List<IMessageHandler> hs = allHandlers.get(msg.getTopic());
				if (hs == null){
					Logger.warn("There are no handlers defined for message '" + msg.getTopic() + "'");
				} else {
					for(IMessageHandler handler : hs){
						handler.process(sender, msg);
						if (msg.isConsumed()) break;
					}
				}
			}
			
		};
		if (uiTaskService != null)
			uiTaskService.runDelayed(task, 1);
		else
			task.run();
	}
	@Override
	public Class<?>[] getDependencyServices() {
		return new Class<?>[]{ IUITaskService.class };
	}
	@Override
	public void start() {
		
	}
	@Override
	public void stop() {
		allHandlers.clear();
	}

}
