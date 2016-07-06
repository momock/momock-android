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
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.cert.X509Certificate;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.json.JSONObject;

import com.momock.http.wget.SpeedInfo;
import com.momock.http.wget.WGet;
import com.momock.http.wget.info.DownloadInfo;
import com.momock.http.wget.info.DownloadInfo.Part;
import com.momock.http.wget.info.ex.DownloadMultipartError;

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

	public static String getUrlParameter(String url, String key) {
		Map<String, String> params = getUrlParameters(url);
		return params.get(key);
	}

	public static String getUrlPath(String url) {
		if (url == null) return null;
		int pos1 = url.indexOf("://");
		if (pos1 == -1) return null;
		pos1 += 3;
		int pos2 = url.indexOf('?');
		return pos2 > pos1 ? url.substring(pos1, pos2) : url.substring(pos1);
	}
	
	public static Map<String, String> getUrlParameters(String url) {
	    Map<String, String> params = new LinkedHashMap<String, String>();
		if (url == null || url.indexOf('?') == -1) return params;
	    String query = url.substring(url.indexOf('?') + 1);
	    String[] pairs = query.split("&");
	    for (String pair : pairs) {
	        int idx = pair.indexOf("=");
	        try {
				params.put(URLDecoder.decode(pair.substring(0, idx), "UTF-8"), URLDecoder.decode(pair.substring(idx + 1), "UTF-8"));
			} catch (UnsupportedEncodingException e) {
				Logger.error(e);
			}
	    }
	    return params;
	}
	
	public static String getFullUrl(String url, Map<String, String> params) {
		if (url == null) return null;
		if (params == null)
			return url;
		url += (url.lastIndexOf('?') == -1 ? "?" : "&");
		for(String key : params.keySet()){
			try {
				url += URLEncoder.encode(key, "UTF-8") + "=" + URLEncoder.encode(params.get(key), "UTF-8") + "&";
			} catch(Exception e){
				Logger.error(e);
			}
		}
		return url;
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
	public static Response doPost(String url, Map<String, String> params, JSONObject body) {
		return doRequest(getFullUrl(url, params), body, HTTP_POST);
	}
	public static long download(String url, String file){
		return download(url, new File(file));
	}
	/*
	public static int download(String url, File file){
		HttpURLConnection connection = null;
		int length = 0;
		try {
			URL httpURL = new URL(url);
			connection = (HttpURLConnection) httpURL.openConnection();
			String ua = System.getProperty("http.agent");
			//Logger.info("UA:" + ua);
			connection.setRequestProperty("User-Agent", ua == null ? "Android" : ua);
			connection.setConnectTimeout(60000);
			connection.setReadTimeout(60000 * 5);
			length = connection.getContentLength();
			FileHelper.copy(connection.getInputStream(), file);
			connection = null;
			
		} catch (Exception e) {			
			Logger.error(e);
		}
		return length;
	}
	*/

    public static String formatSpeed(int bytes) {
        String str = "";
        float speed = bytes;
        if (speed < 1000000) {
            speed /= 1024;
            str += String.format("%.02f", speed) + " KB/s";
        } else {
            speed /= 1024 * 1024;
            str += String.format("%.02f", speed) + " MB/s";
        }
        return str;
    }

	public static long download(final String url, File file){
		if (url == null || file == null) return -1;

		try {
			final AtomicBoolean stop = new AtomicBoolean(false);
			final DownloadInfo info = new DownloadInfo(new URL(url));
			final SpeedInfo speedInfo = new SpeedInfo();

			Runnable notify = new Runnable() {
				long last = 0;
				@Override
				public void run() {
					switch (info.getState()) {
					case EXTRACTING:
					case EXTRACTING_DONE:
					case DONE:
						Logger.debug(url + ":" + info.getState());
						break;
					case RETRYING:
						Logger.debug(url + ":" + info.getState() + " " + info.getDelay());
						break;
					case DOWNLOADING:
						speedInfo.step(info.getCount());
                        long now = System.currentTimeMillis();
                        if (now - 1000 > last) {
                            last = now;
                            float p = info.getCount() / (float) info.getLength();

							Logger.debug(url + ":" + (int)(p * 100) + "% (" + formatSpeed(speedInfo.getCurrentSpeed()) + "/" + formatSpeed(speedInfo.getAverageSpeed()) + ")");						
                        }
						break;
					default:
						break;
					}
				}
			};
			info.extract(stop, notify);
			if (info.getLength() == null || info.getLength() <= 0) return -1;
			info.enableMultipart();
			if (file.exists())
				file.delete();
			WGet w = new WGet(info, file);
			speedInfo.start(0);
			w.download(stop, notify);
			if (info.getCount() == info.getLength())
				return info.getLength();
			else {
				if (file.exists())
					file.delete();
				return -1;
			}
		} catch (DownloadMultipartError e) {
			Logger.error(e);
			for (Part p : e.getInfo().getParts()) {
				Throwable ee = p.getException();
				if (ee != null)
					ee.printStackTrace();
			}
		} catch (RuntimeException e) {
			Logger.error(e);
		} catch (Exception e) {
			Logger.error(e);
		}
		return -1;
	}
	public static Response upload(String url, InputStream is){
		Response response = new Response();
		String boundary = Long.toHexString(System.currentTimeMillis()); 
		HttpURLConnection connection = null;
		try{
			URL httpURL = new URL(url);
			connection = (HttpURLConnection) httpURL.openConnection();
			String ua = System.getProperty("http.agent");
			//Logger.info("UA:" + ua);
			connection.setRequestProperty("User-Agent", ua == null ? "Android" : ua);
			connection.setConnectTimeout(60000);
			connection.setReadTimeout(60000 * 5);
			connection.setRequestMethod("POST");
			connection.setDoOutput(true);
			connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
			byte[] st = ("--" + boundary + "\r\n" + 
					"Content-Disposition: form-data; name=\"file\"; filename=\"data\"\r\n" + 
					"Content-Type: application/octet-stream; charset=UTF-8\r\n" +
					"Content-Transfer-Encoding: binary\r\n\r\n").getBytes();
			byte[] en = ("\r\n--" + boundary + "--\r\n").getBytes();			
            connection.setRequestProperty("Content-Length", String.valueOf(st.length + en.length + is.available()));
			OutputStream os = connection.getOutputStream();
			os.write(st);
            FileHelper.copy(is, os);
            os.write(en);
            os.flush();
            os.close();  
            response.setStatusCode(connection.getResponseCode());
			connection = null;  
		}catch(Exception e){
			Logger.error(e);
		}

		return response;
	}
	static boolean initialized = false;
	static void disableSslCheck(){
		if (initialized) return;
		TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
			public java.security.cert.X509Certificate[] getAcceptedIssuers() {
				return null;
			}

			public void checkClientTrusted(X509Certificate[] certs,
					String authType) {
			}

			public void checkServerTrusted(X509Certificate[] certs,
					String authType) {
			}
		} };

		SSLContext sc;
		try {
			sc = SSLContext.getInstance("SSL");

			sc.init(null, trustAllCerts, new java.security.SecureRandom());
			HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

			// Create all-trusting host name verifier
			HostnameVerifier allHostsValid = new HostnameVerifier() {
				public boolean verify(String hostname, SSLSession session) {
					return true;
				}
			};

			// Install the all-trusting host verifier
			HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
			initialized = true;
		} catch (Exception e) {
			Logger.error(e);
		}

	}
	private static Response doRequest(String url, Object raw, int method) {
		disableSslCheck();
		boolean isJson = raw instanceof JSONObject;
		String body = raw == null ? null : raw.toString();
		Response response = new Response();
		HttpURLConnection connection = null;
		try {
			URL httpURL = new URL(url);
			connection = (HttpURLConnection) httpURL.openConnection();
			String ua = System.getProperty("http.agent");
			//Logger.info("UA:" + ua);
			connection.setRequestProperty("User-Agent", ua == null ? "Android" : ua);
			connection.setConnectTimeout(60000);
			connection.setReadTimeout(60000 * 5);
			connection.setUseCaches(false); 
			if (method == HTTP_POST)
				connection.setRequestMethod("POST");
			if (body != null){
				if (isJson){
					connection.setRequestProperty("Accept", "application/json");
					connection.setRequestProperty("Content-Type", "application/json");
				}
				OutputStream os = connection.getOutputStream();
	            OutputStreamWriter osw = new OutputStreamWriter(os);
	            osw.write(body);
	            osw.flush();
	            osw.close();
			}
			InputStream in = connection.getInputStream();
			response.setBody(FileHelper.readText(in, "UTF-8"));
			response.setStatusCode(connection.getResponseCode());
			in.close();
			connection.disconnect();
			connection = null;
		} catch (Exception e) {
			Logger.error(e);
			try {
				if ((connection != null) && (response.getBody() == null) && (connection.getErrorStream() != null)) {
					response.setBody(FileHelper.readText(connection.getErrorStream(), "UTF-8"));
				} 
			} catch (Exception ex) {
				Logger.error(ex);
			}
		}
		return response;
	}

	private static Response download(String url, Object raw, int method) {
		disableSslCheck();
		boolean isJson = raw instanceof JSONObject;
		String body = raw == null ? null : raw.toString();
		Response response = new Response();
		HttpURLConnection connection = null;
		try {
			URL httpURL = new URL(url);
			connection = (HttpURLConnection) httpURL.openConnection();
			String ua = System.getProperty("http.agent");
			//Logger.info("UA:" + ua);
			connection.setRequestProperty("User-Agent", ua == null ? "Android" : ua);
			connection.setConnectTimeout(60000);
			connection.setReadTimeout(60000 * 5);
			connection.setUseCaches(false); 
			if (method == HTTP_POST)
				connection.setRequestMethod("POST");
			if (body != null){
				if (isJson){
					connection.setRequestProperty("Accept", "application/json");
					connection.setRequestProperty("Content-Type", "application/json");
				}
				OutputStream os = connection.getOutputStream();
	            OutputStreamWriter osw = new OutputStreamWriter(os);
	            osw.write(body);
	            osw.flush();
	            osw.close();
			}
			InputStream in = connection.getInputStream();
			response.setBody(FileHelper.readText(in, "UTF-8"));
			response.setStatusCode(connection.getResponseCode());
			in.close();
			connection.disconnect();
			connection = null;
		} catch (Exception e) {
			Logger.error(e);
			try {
				if ((connection != null) && (response.getBody() == null) && (connection.getErrorStream() != null)) {
					response.setBody(FileHelper.readText(connection.getErrorStream(), "UTF-8"));
				} 
			} catch (Exception ex) {
				Logger.error(ex);
			}
		}
		return response;
	}
}
