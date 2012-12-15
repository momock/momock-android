package com.momock.service;

import org.apache.http.client.HttpClient;

import android.net.http.AndroidHttpClient;

public class HttpService implements IHttpService {

	String userAgent;

	AndroidHttpClient httpClient = null;
	
	public HttpService(){
		
	}
	public HttpService(String userAgent){
		this.userAgent = userAgent == null ? "Android" : userAgent;
	}
	@Override
	public void start() {
		httpClient = AndroidHttpClient.newInstance(userAgent);
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

}
