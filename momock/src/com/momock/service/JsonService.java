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

import java.io.UnsupportedEncodingException;

import javax.inject.Inject;

import org.apache.http.Header;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import org.json.JSONObject;

import com.momock.event.IEventHandler;
import com.momock.http.HttpSession;
import com.momock.http.HttpSession.StateChangedEventArgs;
import com.momock.util.Logger;

public class JsonService implements IJsonService{
	static final String JSON_HEADER_KEY = "Content-Type";
	static final String JSON_HEADER_VAL = "application/json";
	@Inject
	IHttpService httpService;
	public JsonService(){
		
	}
	public JsonService(IHttpService httpService){
		this.httpService = httpService;
	}
	@Override
	public void get(String url, final IEventHandler<JsonEventArgs> handler) {
		Logger.check(httpService != null, "The httpService must not be null!");
		final HttpSession session = httpService.get(url);
		session.getStateChangedEvent().addEventHandler(new IEventHandler<StateChangedEventArgs>(){

			@Override
			public void process(Object sender, StateChangedEventArgs args) {
				if (args.getState() == HttpSession.STATE_FINISHED){
					JsonEventArgs a = new JsonEventArgs(session.getResultAsString(null), session.getError());
					handler.process(session, a);
				}
			}
			
		});
		session.start();
	}

	@Override
	public void post(String url, JSONObject json, final IEventHandler<JsonEventArgs> handler) {
		post(url, json.toString(), handler);
	}

	@Override
	public void post(String url, String json, final IEventHandler<JsonEventArgs> handler) { 
		Logger.check(httpService != null, "The httpService must not be null!");
		StringEntity entity = null;
		Header[] headers = new Header[]{new BasicHeader(JSON_HEADER_KEY, JSON_HEADER_VAL)};
		try {
			entity = new StringEntity(json, "UTF-8");
		} catch (UnsupportedEncodingException e) {
		}
		final HttpSession session = httpService.post(url, headers, entity);
		session.getStateChangedEvent().addEventHandler(new IEventHandler<StateChangedEventArgs>(){

			@Override
			public void process(Object sender, StateChangedEventArgs args) {
				if (args.getState() == HttpSession.STATE_FINISHED){
					JsonEventArgs a = new JsonEventArgs(session.getResultAsString(null), session.getError());
					handler.process(session, a);
				}
			}
			
		});
		session.start();
	}

	@Override
	public void put(String url, JSONObject json, final IEventHandler<JsonEventArgs> handler) {
		put(url, json.toString(), handler);
	}

	@Override
	public void put(String url, String json, final IEventHandler<JsonEventArgs> handler) {
		Logger.check(httpService != null, "The httpService must not be null!");
		StringEntity entity = null;
		Header[] headers = new Header[]{new BasicHeader(JSON_HEADER_KEY, JSON_HEADER_VAL)};
		try {
			entity = new StringEntity(json, "UTF-8");
		} catch (UnsupportedEncodingException e) {
		}
		final HttpSession session = httpService.put(url, headers, entity);
		session.getStateChangedEvent().addEventHandler(new IEventHandler<StateChangedEventArgs>(){

			@Override
			public void process(Object sender, StateChangedEventArgs args) {
				if (args.getState() == HttpSession.STATE_FINISHED){
					JsonEventArgs a = new JsonEventArgs(session.getResultAsString(null), session.getError());
					handler.process(session, a);
				}
			}
			
		});
		session.start();
	}

	@Override
	public void delete(String url, final IEventHandler<JsonEventArgs> handler) {
		Logger.check(httpService != null, "The httpService must not be null!");
		final HttpSession session = httpService.delete(url);
		session.getStateChangedEvent().addEventHandler(new IEventHandler<StateChangedEventArgs>(){

			@Override
			public void process(Object sender, StateChangedEventArgs args) {
				if (args.getState() == HttpSession.STATE_FINISHED){
					JsonEventArgs a = new JsonEventArgs(session.getResultAsString(null), session.getError());
					handler.process(session, a);
				}
			}
			
		});
		session.start();
	}

	@Override
	public Class<?>[] getDependencyServices() {
		return new Class<?>[]{ IHttpService.class };
	}

	@Override
	public void start() {
		
	}

	@Override
	public void stop() {
		
	}
	@Override
	public boolean canStop() {
		return true;
	}

}
