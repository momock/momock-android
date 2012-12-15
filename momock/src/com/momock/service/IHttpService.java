package com.momock.service;

import org.apache.http.client.HttpClient;

public interface IHttpService extends IService{
	HttpClient getHttpClient();
}
