package com.momock.service;

import java.io.File;

public class HttpSession {
	public static class Callback{
		public void onStart(HttpSession session){
			
		}
		public void onHeaderReceived(HttpSession session){
			
		}
		public void onContentReceiving(HttpSession session){
			
		}
		public void onContentReceived(HttpSession session){
			
		}
		public void onError(HttpSession session){
			
		}
		public void onFinish(HttpSession session){
			
		}
	}

	String url;
	int downloadedLength = 0;
	int contentLength = -1;
	Throwable error = null;
	File file = null;
	Callback callback;
	
	public HttpSession(String url){
		this.url = url;
	}
	public String getUrl() {
		return url;
	}
	public int getDownloadedLength() {
		return downloadedLength;
	}
	public void setDownloadedLength(int downloadedLength) {
		this.downloadedLength = downloadedLength;
	}
	public int getContentLength() {
		return contentLength;
	}
	public void setContentLength(int contentLength) {
		this.contentLength = contentLength;
	}
	public Throwable getError() {
		return error;
	}
	public void setError(Throwable error) {
		this.error = error;
	}
	public File getFile() {
		return file;
	}
	public void setFile(File file) {
		this.file = file;
	}
	public Callback getCallback() {
		return callback;
	}
	public void setCallback(Callback callback) {
		this.callback = callback;
	}
}
