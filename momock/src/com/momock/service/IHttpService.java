package com.momock.service;

import java.io.File;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.HttpClient;

import com.momock.data.IDataMap;
import com.momock.http.HttpSession;

public interface IHttpService extends IService{
	HttpClient getHttpClient();
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
