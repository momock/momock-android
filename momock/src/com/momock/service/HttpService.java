package com.momock.service;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.LinkedList;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;

import android.net.http.AndroidHttpClient;

import com.momock.data.IDataMap;
import com.momock.http.HttpSession;

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

	String getFullUrl(String url, IDataMap<String, String> params){
		if (params == null) return url;
		List<BasicNameValuePair> lparams = new LinkedList<BasicNameValuePair>();
		for (String key : params.getPropertyNames()) {
			lparams.add(new BasicNameValuePair(key, params.getProperty(key)));
		}
		return url + (url.lastIndexOf('?') == -1 ? "?" : "&") + URLEncodedUtils.format(lparams, "UTF-8");
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
		return new HttpSession(httpClient, url, file);
	}
	@Override
	public HttpSession get(String url) {
		return get(url, null);
	}
	@Override
	public HttpSession get(String url, IDataMap<String, String> params) {
		return get(url, null, params);
	}
	@Override
	public HttpSession get(String url, Header[] headers, IDataMap<String, String> params) {
		HttpGet httpGet = new HttpGet(getFullUrl(url, params));	
		if (headers != null) httpGet.setHeaders(headers);
		return new HttpSession(httpClient, httpGet);
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
		return new HttpSession(httpClient, httpPost);
	}
	@Override
	public HttpSession post(String url, Header[] headers, HttpEntity entity) {
		HttpPost httpPost = new HttpPost(url);
		if (entity != null) httpPost.setEntity(entity);
		if (headers != null) httpPost.setHeaders(headers);
		return new HttpSession(httpClient, httpPost);
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
		return new HttpSession(httpClient, httpPut);
	}
	@Override
	public HttpSession put(String url, Header[] headers, HttpEntity entity) {
		HttpPut httpPut = new HttpPut(url);
		if (entity != null) httpPut.setEntity(entity);
		if (headers != null) httpPut.setHeaders(headers);
		return new HttpSession(httpClient, httpPut);
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
		return new HttpSession(httpClient, httpDelete);
	}
	@Override
	public Class<?>[] getDependencyServices() {
		return null;
	}

}
