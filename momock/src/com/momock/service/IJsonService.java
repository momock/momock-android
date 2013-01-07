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

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.momock.event.EventArgs;
import com.momock.event.IEventHandler;
import com.momock.util.Logger;

public interface IJsonService extends IService{
	public static class JsonEventArgs extends EventArgs {
		String response;
		Throwable error;

		public JsonEventArgs(String response, Throwable error) {
			this.response = response == null ? "" : response.trim();
			this.error = error;
		}

		public String getResponse() {
			return response;
		}

		public Object getJson() {
			Object result = null;
			try {
				result = new JSONTokener(response).nextValue();
			} catch (JSONException e) {
				Logger.error(response);
				Logger.error(e);
			}
			if (result == null) 
				result = response;
			return result;
		}
	}

	void get(String url, IEventHandler<JsonEventArgs> handler);

	void post(String url, JSONObject json, IEventHandler<JsonEventArgs> handler);

	void put(String url, JSONObject json, IEventHandler<JsonEventArgs> handler);

	void post(String url, String json, IEventHandler<JsonEventArgs> handler);

	void put(String url, String json, IEventHandler<JsonEventArgs> handler);

	void delete(String url, IEventHandler<JsonEventArgs> handler);
}
