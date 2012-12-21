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
package com.momock.util;

import java.util.LinkedList;
import java.util.List;

import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;

import com.momock.data.IDataMap;

public class HttpHelper {

	public static String getFullUrl(String url, IDataMap<String, String> params){
		if (params == null) return url;
		List<BasicNameValuePair> lparams = new LinkedList<BasicNameValuePair>();
		for (String key : params.getPropertyNames()) {
			lparams.add(new BasicNameValuePair(key, params.getProperty(key)));
		}
		return url + (url.lastIndexOf('?') == -1 ? "?" : "&") + URLEncodedUtils.format(lparams, "UTF-8");
	}
}
