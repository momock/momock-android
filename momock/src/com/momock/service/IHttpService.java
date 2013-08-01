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

import java.io.File;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.HttpClient;

import com.momock.data.IDataMap;
import com.momock.http.HttpSession;

public interface IHttpService extends IService{
	HttpClient getHttpClient();
	void setDefaultHeaders(Header[] headers);
	HttpSession download(String url, File file);
	HttpSession get(String url);
	HttpSession get(String url, IDataMap<String, String> params);
	HttpSession get(String url, Header[] headers, IDataMap<String, String> params);
	HttpSession post(String url);
	HttpSession post(String url, IDataMap<String, String> params);
	HttpSession post(String url, Header[] headers, IDataMap<String, String> params);
	HttpSession post(String url, Header[] headers, HttpEntity entity);
	HttpSession put(String url);
	HttpSession put(String url, IDataMap<String, String> params);
	HttpSession put(String url, Header[] headers, IDataMap<String, String> params);
	HttpSession put(String url, Header[] headers, HttpEntity entity);
	HttpSession delete(String url);
	HttpSession delete(String url, IDataMap<String, String> params);
	HttpSession delete(String url, Header[] headers, IDataMap<String, String> params);
}
