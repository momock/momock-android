package com.momock.service;

import java.net.ContentHandler;
import java.net.URLStreamHandlerFactory;

import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.os.Handler;

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
}
