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
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Constructor;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;

import android.net.http.AndroidHttpClient;

import com.momock.data.IDataMap;
import com.momock.http.HttpSession;
import com.momock.util.Logger;

public class HttpService implements IHttpService {

    private static final int SOCKET_OPERATION_TIMEOUT = 60 * 1000 * 5;
    
	String userAgent;
	
	boolean autoRedirect = true;

	AndroidHttpClient httpClient = null;
	
	@Inject
	IUITaskService uiTaskService;
	@Inject
	IAsyncTaskService asyncTaskService;
	
	public HttpService(){
		this(null);
	}
	public HttpService(String userAgent){
		this(userAgent, null);
	}
	public HttpService(String userAgent, IUITaskService uiTaskService){
		this(userAgent, uiTaskService, null);
	}
	public HttpService(String userAgent, IUITaskService uiTaskService, IAsyncTaskService asyncTaskService){
		this(userAgent, true, uiTaskService, asyncTaskService);
	}
	public HttpService(String userAgent, boolean autoRedirect, IUITaskService uiTaskService, IAsyncTaskService asyncTaskService){
		this.userAgent = userAgent == null ? "Android" : userAgent;
		this.uiTaskService = uiTaskService;
		this.asyncTaskService = asyncTaskService;
		this.autoRedirect = autoRedirect;
	}
	
	class SSLSocketFactoryEx extends SSLSocketFactory {

		SSLContext sslContext = SSLContext.getInstance("TLS");

		public SSLSocketFactoryEx(KeyStore truststore)
				throws NoSuchAlgorithmException, KeyManagementException,
				KeyStoreException, UnrecoverableKeyException {
			super(truststore);

			TrustManager tm = new X509TrustManager() {

				@Override
				public java.security.cert.X509Certificate[] getAcceptedIssuers() {
					return null;
				}

				@Override
				public void checkClientTrusted(
						java.security.cert.X509Certificate[] chain, String authType)
						throws java.security.cert.CertificateException {

				}

				@Override
				public void checkServerTrusted(
						java.security.cert.X509Certificate[] chain, String authType)
						throws java.security.cert.CertificateException {

				}
			};

			sslContext.init(null, new TrustManager[] { tm }, null);
		}

		@Override
		public Socket createSocket(Socket socket, String host, int port,
				boolean autoClose) throws IOException, UnknownHostException {
			return sslContext.getSocketFactory().createSocket(socket, host, port,
					autoClose);
		}

		@Override
		public Socket createSocket() throws IOException {
			return sslContext.getSocketFactory().createSocket();
		}
	}

	@Override
	public void start() {
		
        try{

    		HttpParams params = new BasicHttpParams();
            HttpConnectionParams.setStaleCheckingEnabled(params, false);
            HttpConnectionParams.setConnectionTimeout(params, SOCKET_OPERATION_TIMEOUT);
            HttpConnectionParams.setSoTimeout(params, SOCKET_OPERATION_TIMEOUT);
            HttpConnectionParams.setSocketBufferSize(params, 8192);
            HttpClientParams.setRedirecting(params, autoRedirect);
            HttpProtocolParams.setUserAgent(params, userAgent);
            SchemeRegistry schemeRegistry = new SchemeRegistry();
            schemeRegistry.register(new Scheme("http",
                    PlainSocketFactory.getSocketFactory(), 80));

	        KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
			trustStore.load(null, null);
			SSLSocketFactory sf = new SSLSocketFactoryEx(trustStore);
			sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
	
	        schemeRegistry.register(new Scheme("https", sf, 443));
	        
	        ClientConnectionManager manager =
	                new ThreadSafeClientConnManager(params, schemeRegistry);
	        
			Class<?> clazz = Class.forName("android.net.http.AndroidHttpClient"); 
			Constructor<?> constructor = clazz.getDeclaredConstructors()[0];  
			constructor.setAccessible(true);  
			httpClient = (AndroidHttpClient)constructor.newInstance(manager, params); 
        }catch(Exception e){
        	Logger.error(e);
        	httpClient = AndroidHttpClient.newInstance(userAgent);
        }
	}

	@Override
	public void stop() {
		httpClient.close();
		httpClient = null;
	}

	@Override
	public HttpClient getHttpClient() {
		return httpClient;
	}
	
	@Override
	public void setDefaultHeaders(Header[] hs){
		if (hs != null){
			List<Header> headers = new ArrayList<Header>();  
			for(Header h : hs){
				headers.add(h);				
			}
			httpClient.getParams().setParameter(ClientPNames.DEFAULT_HEADERS, headers);
		}
	}
	HttpEntity getHttpEntity(IDataMap<String, String> params){
		if (params == null) return null;
		List<BasicNameValuePair> lparams = new LinkedList<BasicNameValuePair>();
		for (String key : params.getPropertyNames()) {
			lparams.add(new BasicNameValuePair(key, params.getProperty(key)));
		}
		try {
			return new UrlEncodedFormEntity(lparams, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			return null;
		}
	}
	@Override
	public HttpSession download(String url, File file) {
		return new HttpSession(httpClient, url, file, uiTaskService, asyncTaskService);
	}
	@Override
	public HttpSession get(String url) {
		return get(url, null);
	}
	@Override
	public HttpSession get(String url, IDataMap<String, String> params) {		
		return get(url, null, params);
	}

	public static String getParamString(Map<String, String> params){
		List<BasicNameValuePair> lparams = new LinkedList<BasicNameValuePair>();
		for (String key : params.keySet()) {
			lparams.add(new BasicNameValuePair(key, params.get(key)));			
		}
		return URLEncodedUtils.format(lparams, "UTF-8");
	}

	String getFullUrl(String url, IDataMap<String, String> params){
		if (params == null) return url;
		Map<String, String> ps = new HashMap<String, String>();
		for(String key : params.getPropertyNames()){
			ps.put(key, params.getProperty(key));
		}
		if (url == null) return null;
		return url + (url.lastIndexOf('?') == -1 ? "?" : "&") + getParamString(ps);
	}
	@Override
	public HttpSession get(String url, Header[] headers, IDataMap<String, String> params) {
		HttpGet httpGet = new HttpGet(getFullUrl(url, params));	
		if (headers != null) httpGet.setHeaders(headers);
		return new HttpSession(httpClient, httpGet, uiTaskService, asyncTaskService);
	}
	@Override
	public HttpSession post(String url) {
		return post(url, null);
	}
	@Override
	public HttpSession post(String url, IDataMap<String, String> params) {
		return post(url, null, params);
	}
	@Override
	public HttpSession post(String url, Header[] headers, IDataMap<String, String> params) {
		HttpPost httpPost = new HttpPost(url);
		HttpEntity entity = getHttpEntity(params);
		if (entity != null) httpPost.setEntity(entity);
		if (headers != null) httpPost.setHeaders(headers);
		return new HttpSession(httpClient, httpPost, uiTaskService, asyncTaskService);
	}
	@Override
	public HttpSession post(String url, Header[] headers, HttpEntity entity) {
		HttpPost httpPost = new HttpPost(url);
		if (entity != null) httpPost.setEntity(entity);
		if (headers != null) httpPost.setHeaders(headers);
		return new HttpSession(httpClient, httpPost, uiTaskService, asyncTaskService);
	}
	@Override
	public HttpSession put(String url) {
		return put(url, null);
	}
	@Override
	public HttpSession put(String url, IDataMap<String, String> params) {
		return put(url, null, params);
	}
	@Override
	public HttpSession put(String url, Header[] headers, IDataMap<String, String> params) {
		HttpPut httpPut = new HttpPut(url);
		HttpEntity entity = getHttpEntity(params);
		if (entity != null) httpPut.setEntity(entity);
		if (headers != null) httpPut.setHeaders(headers);
		return new HttpSession(httpClient, httpPut, uiTaskService, asyncTaskService);
	}
	@Override
	public HttpSession put(String url, Header[] headers, HttpEntity entity) {
		HttpPut httpPut = new HttpPut(url);
		if (entity != null) httpPut.setEntity(entity);
		if (headers != null) httpPut.setHeaders(headers);
		return new HttpSession(httpClient, httpPut, uiTaskService, asyncTaskService);
	}
	@Override
	public HttpSession delete(String url) {
		return delete(url, null);
	}
	@Override
	public HttpSession delete(String url, IDataMap<String, String> params) {
		return delete(url, null, params);
	}
	@Override
	public HttpSession delete(String url, Header[] headers,	IDataMap<String, String> params) {
		HttpDelete httpDelete = new HttpDelete(getFullUrl(url, params));	
		if (headers != null) httpDelete.setHeaders(headers);
		return new HttpSession(httpClient, httpDelete, uiTaskService, asyncTaskService);
	}
	@Override
	public Class<?>[] getDependencyServices() {
		return new Class<?>[]{ IUITaskService.class };
	}
	@Override
	public boolean canStop() {
		return true;
	}
	@Override
	public HttpSession postJson(String url, String json) {
		StringEntity entity = null;
		try{
			entity = new StringEntity(json, "UTF-8");			
		}catch(Exception e){
			Logger.error(e);
		}
		return post(url, new Header[]{new BasicHeader("Content-Type", "application/json")},	entity);
	}
	@Override
	public HttpSession putJson(String url, String json) {
		StringEntity entity = null;
		try{
			entity = new StringEntity(json, "UTF-8");			
		}catch(Exception e){
			Logger.error(e);
		}
		return put(url, new Header[]{new BasicHeader("Content-Type", "application/json")},	entity);
	}
}
