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

import java.net.ContentHandler;
import java.net.URLStreamHandlerFactory;

import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.os.Handler;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.google.android.imageloader.ImageLoader;
import com.momock.holder.ImageHolder;
import com.momock.util.Logger;

public class ImageService extends ImageLoader implements IImageService{
	public ImageService() {
		super();
	}
	public ImageService(ContentHandler bitmapHandler,
			ContentHandler prefetchHandler) {
		super(bitmapHandler, prefetchHandler);
	}
	public ImageService(ContentResolver resolver) {
		super(resolver);
	}
	public ImageService(int taskLimit,
			URLStreamHandlerFactory streamFactory,
			ContentHandler bitmapHandler, ContentHandler prefetchHandler,
			long cacheSize, Handler handler) {
		super(taskLimit, streamFactory, bitmapHandler, prefetchHandler, cacheSize,
				handler);
	}
	public ImageService(int taskLimit) {
		super(taskLimit);
	}
	public ImageService(long cacheSize) {
		super(cacheSize);
	}
	public ImageService(URLStreamHandlerFactory factory) {
		super(factory);
	}
	public void load(ImageHolder holder, final ImageSetter handler) {
		Logger.check(holder != null, "Parameters holder and url cannot be null!");
        String url = holder.getUri();
        Bitmap bitmap = getBitmap(url);
        ImageError error = getError(url);
        if (bitmap != null) {
        	handler.setImage(bitmap);
        } else {
            if (error != null) {
            	handler.setImage(null);
            } else {
            	ImageCallback callback = new ImageCallback(){

					@Override
					public boolean unwanted() {
						return false;
					}

					@Override
					public void send(String url, Bitmap bitmap,
							ImageError error) {
						if (error != null)
							Logger.error(error.getCause().getMessage());
						handler.setImage(bitmap);				
					}
                	
                };
                ImageRequest request = new ImageRequest(url, callback, true);
                enqueueRequest(request);
            }
        }
    }
	@Override
	public void load(BaseAdapter adapter, ImageView view, String url) {
		bind(adapter, view, url);
	}
	@Override
	public void load(ImageView view, String url) {
		bind(view, url, null);
	}
}
