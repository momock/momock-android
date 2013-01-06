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

import com.momock.message.IMessageHandler;
import com.momock.message.Message;

public interface IMessageService extends IService {
	void addHandler(String topic, IMessageHandler handler);

	void removeHandler(String topic, IMessageHandler handler);

	void send(Object sender, String topic);

	void send(Object sender, String topic, Object data);

	void send(final Object sender, final Message msg);
}
