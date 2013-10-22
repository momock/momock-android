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

import java.io.File;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;

public class HttpHelper {
	public static class Response {
		private int statusCode;
		private String body;
		public int getStatusCode() {
			return statusCode;
		}
		public void setStatusCode(int statusCode) {
			this.statusCode = statusCode;
		}
		public String getBody() {
			return body;
		}
		public void setBody(String body) {
			this.body = body;
		}
	}

	public static final int HTTP_GET = 1;
	public static final int HTTP_POST = 2;

	public static String getParamString(Map<String, String> params){
		List<BasicNameValuePair> lparams = new LinkedList<BasicNameValuePair>();
		for (String key : params.keySet()) {
			lparams.add(new BasicNameValuePair(key, params.get(key)));			
		}
		return URLEncodedUtils.format(lparams, "UTF-8");
	}
	public static String getFullUrl(String url, Map<String, String> params) {
		if (url == null) return null;
		if (params == null)
			return url;
		return url + (url.lastIndexOf('?') == -1 ? "?" : "&") + getParamString(params);
	}

	public static Response doGet(String url, Map<String, String> params) {
		return doRequest(getFullUrl(url, params), null, HTTP_GET);
	}

	public static Response doPost(String url, Map<String, String> params) {
		return doRequest(getFullUrl(url, params), null, HTTP_POST);
	}
	public static Response doPost(String url, Map<String, String> params, String body) {
		return doRequest(getFullUrl(url, params), body, HTTP_POST);
	}
	public static int download(String url, String file){
		return download(url, new File(file));
	}
	public static int download(String url, File file){
		HttpURLConnection connection = null;
		int length = 0;
		try {
			URL httpURL = new URL(url);
			connection = (HttpURLConnection) httpURL.openConnection();
			connection.setConnectTimeout(15000);
			connection.setReadTimeout(30000);
			length = connection.getContentLength();
			FileHelper.copy(connection.getInputStream(), file);
			connection = null;
			
		} catch (Exception e) {			
			Logger.error(e);
		}
		return length;
	}
	private static Response doRequest(String url, String body, int method) {
		Response response = new Response();
		HttpURLConnection connection = null;
		try {
			URL httpURL = new URL(url);
			connection = (HttpURLConnection) httpURL.openConnection();
			connection.setConnectTimeout(15000);
			connection.setReadTimeout(30000);
			if (method == HTTP_POST)
				connection.setRequestMethod("POST");
			if (body != null){
				OutputStream os = connection.getOutputStream();
	            OutputStreamWriter osw = new OutputStreamWriter(os);
	            osw.write(body);
	            osw.flush();
	            osw.close();
			}
			response.setBody(FileHelper.readText(connection.getInputStream(), "UTF-8"));
			response.setStatusCode(connection.getResponseCode());
			connection = null;
		} catch (Exception e) {
			try {
				if ((connection != null) && (response.getBody() == null)) {
					response.setBody(FileHelper.readText(connection.getErrorStream(), "UTF-8"));
				}
			} catch (Exception ex) {
				Logger.error(ex);
			}
		}
		return response;
	}
}
