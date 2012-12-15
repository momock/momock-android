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

import android.graphics.Bitmap;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.momock.event.EventArgs;
import com.momock.event.IEventHandler;

public interface IImageService extends IService{
	public static final String PREFIX_FILE = "file://";
	public static final String PREFIX_RES = "res://";
	public static final String PREFIX_ASSETS = "assets://";
	public static final String PREFIX_HTTP = "http://";
	public static final String PREFIX_HTTPS = "https://";
	
	public static class ImageEventArgs extends EventArgs {
		String uri;
		Bitmap bitmap;
		Throwable error;
		public String getUri() {
			return uri;
		}
		public Bitmap getBitmap() {
			return bitmap;
		}
		public Throwable getError() {
			return error;
		}
		public ImageEventArgs(String uri, Bitmap bitmap, Throwable error){
			this.uri = uri;
			this.bitmap = bitmap;
			this.error = error;
		}
	}
	
	void addImageEventHandler(String uri, IEventHandler<ImageEventArgs> handler);
	
	void removeImageEventHandler(String uri, IEventHandler<ImageEventArgs> handler);
	
	String getFullUri(String uri, int width, int height);
	
	boolean isRemote(String uri);
	
	File getCacheOf(String fullUri);
	
	Bitmap loadBitmap(String fullUri);	

	Bitmap loadBitmap(String fullUri, boolean highPriority);	
	
	void bind(String fullUri, IEventHandler<ImageEventArgs> handler, boolean highPriority);
	
	void bind(String fullUri, ImageView view, boolean highPriority);
	
	void bind(String fullUri, ViewGroup viewGroup, boolean highPriority);
	
	void bind(String fullUri, BaseAdapter adapter, boolean highPriority);
}
