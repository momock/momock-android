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
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import android.graphics.Bitmap;
import android.widget.ImageView;

import com.momock.binder.IContainerBinder;
import com.momock.cache.BitmapCache;
import com.momock.event.Event;
import com.momock.event.IEvent;
import com.momock.event.IEventHandler;
import com.momock.http.HttpSession;
import com.momock.http.HttpSession.StateChangedEventArgs;
import com.momock.util.Convert;
import com.momock.util.ImageHelper;
import com.momock.util.Logger;

public class ImageService implements IImageService {
	Map<String, IEvent<ImageEventArgs>> allImageHandlers = new HashMap<String, IEvent<ImageEventArgs>>();
	BitmapCache<String> bitmapCache;
	Map<String, HttpSession> sessions = new HashMap<String, HttpSession>();
	@Inject
	IHttpService httpService;
	@Inject
	ICacheService cacheService;
	public ImageService(){
		this(1024 * 1024 * 16);
	}
	public ImageService(long cacheSize){
		bitmapCache = new BitmapCache<String>(cacheSize);
	}
	public ImageService(long cacheSize, IHttpService httpService, ICacheService cacheService){
		bitmapCache = new BitmapCache<String>(cacheSize);
		this.httpService = httpService;
		this.cacheService = cacheService;
	}
	public Bitmap getBitmap(String fullUri){
		return bitmapCache.get(fullUri);
	}
	@Override
	public void addImageEventHandler(String fullUri,
			IEventHandler<ImageEventArgs> handler) {
		IEvent<ImageEventArgs> evt;
		if (allImageHandlers.containsKey(fullUri)) {
			evt = allImageHandlers.get(fullUri);
		} else {
			evt = new Event<ImageEventArgs>();
			allImageHandlers.put(fullUri, evt);
		}
		evt.addEventHandler(handler);
	}

	@Override
	public void removeImageEventHandler(String fullUri,
			IEventHandler<ImageEventArgs> handler) {
		IEvent<ImageEventArgs> evt;
		if (allImageHandlers.containsKey(fullUri)) {
			evt = allImageHandlers.get(fullUri);
			evt.removeEventHandler(handler);
		}
	}

	@Override
	public File getCacheOf(String fullUri){
		Logger.check(cacheService != null, "The cacheService must not be null!");
		return cacheService.getCacheOf(getClass().getName(), fullUri);
	}
	
	@Override
	public void clearCache(){
		Logger.debug("Clear cache in ImageService!");
		bitmapCache.clear();
		System.gc();		
	}
	
	@Override
	public Bitmap loadBitmap(final String fullUri) {
		try {
			try {
				return load(fullUri);
			} catch (OutOfMemoryError e) {
				Logger.error(e.getMessage());
				clearCache();
				return load(fullUri);
			}
		} catch (Throwable t) {
			Logger.error(t.getMessage());
			return null;
		}
	}
	protected Bitmap load(final String fullUri) {
		final int expectedWidth;
		final int expectedHeight;
		String uri = fullUri;
		int pos = fullUri.lastIndexOf('#');
		if (pos > 0) {
			int pos2 = fullUri.lastIndexOf('x');
			Logger.check(pos2 > pos, "The image uri is not correct!");
			expectedWidth = Convert.toInteger(fullUri.substring(pos + 1, pos2 - pos
					- 1));
			expectedHeight = Convert.toInteger(fullUri.substring(pos2 + 1));
			uri = fullUri.substring(0, pos);
		} else {
			expectedWidth = -1;
			expectedHeight = -1;
		}
		Bitmap bitmap = getBitmap(uri);
		if (bitmap != null) return bitmap;
		if (uri.startsWith(PREFIX_FILE)) {
			bitmap = ImageHelper.fromFile(uri.substring(PREFIX_FILE.length()), expectedWidth, expectedHeight);
		} else if (uri.startsWith(PREFIX_RES)) {
			bitmap = ImageHelper.fromStream(ImageService.class.getResourceAsStream(uri.substring(PREFIX_RES.length())), expectedWidth, expectedHeight);
		} else if (uri.startsWith(PREFIX_HTTP) || uri.startsWith(PREFIX_HTTPS)) {			
			if (bitmap == null) {
				File bmpFile = getCacheOf(fullUri);
				if (bmpFile.exists()){
					bitmap = ImageHelper.fromFile(bmpFile, expectedWidth, expectedHeight);
				} 
				if (bitmap == null) {
					HttpSession session = sessions.get(uri);
					if (session == null){
						Logger.check(httpService != null, "The httpService must not be null!");
						session = httpService.download(uri, bmpFile);
						session.start();
						sessions.put(uri, session);
						session.getStateChangedEvent().addEventHandler(new IEventHandler<StateChangedEventArgs>(){
	
							@Override
							public void process(Object sender,
									StateChangedEventArgs args) {
								if (args.getState() == HttpSession.STATE_FINISHED){
									if (allImageHandlers.containsKey(fullUri)) {
										Bitmap bitmap = null;
										try{
											bitmap = ImageHelper.fromFile(args.getSession().getFile(), expectedWidth, expectedHeight);
										} catch(OutOfMemoryError e){
											Logger.error(e.getMessage());
											clearCache();
											try{
												bitmap = ImageHelper.fromFile(args.getSession().getFile(), expectedWidth, expectedHeight);
											} catch(Throwable t){
												Logger.error(t.getMessage());
											}
										}
										if (bitmap == null){
											if (args.getSession().getError() != null){
												Logger.error("Fails to download image (" + args.getSession().getUrl() + ") : " + args.getSession().getError().getMessage());
											}
											if (args.getSession().isDownloaded()){
												Logger.error("The content in image (" + args.getSession().getUrl() + ") : " + args.getSession().getResultAsString(null));
											}
										} else {
											ImageEventArgs iea = new ImageEventArgs(
													fullUri, bitmap, args.getSession().getError());
											IEvent<ImageEventArgs> evt = allImageHandlers.get(fullUri);
											evt.fireEvent(null, iea);
										}
									}
								}
							}
							
						});
						Logger.debug("Image " + uri + " has been added into the downloading queue. ");		
					}
				}
			}
		}
		if (bitmap != null){
			bitmapCache.put(fullUri, bitmap);
		}
		return bitmap;
	}

	@Override
	public boolean isRemote(String uri) {
		return uri.startsWith(PREFIX_HTTP) || uri.startsWith(PREFIX_HTTPS);
	}

	@Override
	public String getFullUri(String uri, int width, int height) {
		return width > 0 && height > 0 ? uri + "#" + width + "x" + height : uri;
	}


	@Override
	public void bind(String fullUri, IEventHandler<ImageEventArgs> handler) {
		Bitmap bitmap = getBitmap(fullUri);
		if (bitmap != null) {
			ImageEventArgs args = new ImageEventArgs(fullUri, bitmap, null);
			handler.process(this, args);
		} else {
			loadBitmap(fullUri);
			addImageEventHandler(fullUri, handler);
		}
	}
	class ImageViewRefreshHandler implements IEventHandler<ImageEventArgs>{
		WeakReference<ImageView> refImageView; 
		public ImageViewRefreshHandler(ImageView iv){
			refImageView = new WeakReference<ImageView>(iv);
		}
		public ImageView getImageView(){
			return refImageView.get();
		}
		@Override
		public void process(Object sender, ImageEventArgs args) {
			if (refImageView.get() != null)
				refImageView.get().setImageBitmap(args.getBitmap());
		}

	}
	List<ImageViewRefreshHandler> imageViewHandlers = new ArrayList<ImageViewRefreshHandler>();

	@Override
	public void bind(String fullUri, ImageView view) {
		Logger.check(view != null, "Parameter view cannot be null !");
		Bitmap bitmap = getBitmap(fullUri);
		if (bitmap != null)
			view.setImageBitmap(bitmap);
		else {
			loadBitmap(fullUri);
			ImageViewRefreshHandler handler = null;
			Iterator<ImageViewRefreshHandler> it = imageViewHandlers.iterator();
			while(it.hasNext()){
				ImageViewRefreshHandler h = it.next();
				if (h.getImageView() == null) 
					it.remove();
				else if (h.getImageView() == view){
					handler = h;
					break;
				}				
			}
			if (handler == null){
				handler = new ImageViewRefreshHandler(view);
				imageViewHandlers.add(handler);
			}
			addImageEventHandler(fullUri, handler);
		}
	}

	class BinderRefreshHandler implements IEventHandler<ImageEventArgs>{
		public IContainerBinder binder;
		public Object item;
		public BinderRefreshHandler(IContainerBinder binder, Object item){
			this.binder = binder;
			this.item = item;
		}
		@Override
		public void process(Object sender, ImageEventArgs args) {
			if (binder.getContainerView() != null && binder.getViewOf(item) != null){
				binder.getItemBinder().onCreateItemView(binder.getViewOf(item), item, binder);
			}
		}

	}
	List<BinderRefreshHandler> binderHandlers = new ArrayList<BinderRefreshHandler>();
	@Override
	public void bind(String fullUri, IContainerBinder binder, Object item) {
		Logger.check(binder != null && item != null, "Parameter binder and item cannot be null !");
		Bitmap bitmap = loadBitmap(fullUri);
		if (bitmap == null){
			BinderRefreshHandler handler = null;
			Iterator<BinderRefreshHandler> it = binderHandlers.iterator();
			while(it.hasNext()){
				BinderRefreshHandler h = it.next();
				if (h.binder.getContainerView() == null) 
					it.remove();
				else if (h.binder == binder && h.item == item){
					handler = h;
					break;
				}				
			}
			if (handler == null){
				handler = new BinderRefreshHandler(binder, item);
				binderHandlers.add(handler);
			}
			addImageEventHandler(fullUri, handler);
		}
	}

	@Override
	public void start() {	
	}

	@Override
	public void stop() {
		allImageHandlers.clear();
		imageViewHandlers.clear();
		binderHandlers.clear();
	}
	@Override
	public Class<?>[] getDependencyServices() {
		return new Class<?>[]{IHttpService.class, ICacheService.class};
	}
}
