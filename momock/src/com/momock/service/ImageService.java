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
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.graphics.Bitmap;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.momock.app.App;
import com.momock.cache.ICache;
import com.momock.cache.SimpleCache;
import com.momock.event.Event;
import com.momock.event.IEvent;
import com.momock.event.IEventHandler;
import com.momock.holder.ImageHolder;
import com.momock.net.HttpSession;
import com.momock.net.HttpSession.StateChangedEventArgs;
import com.momock.util.Convert;
import com.momock.util.ImageHelper;
import com.momock.util.Logger;
import com.momock.widget.IPlainAdapterView;

public class ImageService implements IImageService {
	Downloader downloader;
	Map<String, IEvent<ImageEventArgs>> allImageHandlers = new HashMap<String, IEvent<ImageEventArgs>>();
	ICache<String, Bitmap> bitmapCache = new SimpleCache<String, Bitmap>();
	public ImageService(){
		this(5, null);
	}
	public ImageService(int maxTaskCount, String userAgent){
		downloader = new Downloader(maxTaskCount);
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
		ICacheService cacheService = App.get().getService(ICacheService.class);
		return cacheService.getCacheOf(getClass().getName(), fullUri);
	}
	
	@Override
	public Bitmap loadBitmap(final String fullUri) {
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
			bitmap = ImageHelper.fromFile(uri.substring(PREFIX_FILE.length()),
					expectedWidth, expectedHeight);
		} else if (uri.startsWith(PREFIX_RES)) {
			bitmap = ImageHelper.fromStream(ImageHolder.class
					.getResourceAsStream(uri.substring(PREFIX_RES.length())),
					expectedWidth, expectedHeight);
		} else if (uri.startsWith(PREFIX_ASSETS)) {
			try {
				bitmap = ImageHelper.fromStream(App.get().getResources()
						.getAssets()
						.open(uri.substring(PREFIX_ASSETS.length())),
						expectedWidth, expectedHeight);
			} catch (IOException e) {
				Logger.error(e.getMessage());
			}
		} else if (uri.startsWith(PREFIX_HTTP) || uri.startsWith(PREFIX_HTTPS)) {			
			if (bitmap == null) {
				File bmpFile = getCacheOf(fullUri);
				if (bmpFile.exists()){
					bitmap = ImageHelper.fromFile(bmpFile, expectedWidth, expectedHeight);
				} 
				if (bitmap == null) {
					HttpSession session = downloader.getSession(uri);
					if (session == null){
						session = downloader.newSession(uri, bmpFile);
						downloader.addSession(session);
						session.getStateChangedEvent().addEventHandler(new IEventHandler<StateChangedEventArgs>(){
	
							@Override
							public void process(Object sender,
									StateChangedEventArgs args) {
								if (args.getState() == HttpSession.STATE_FINISHED){
									if (allImageHandlers.containsKey(fullUri)) {
										Bitmap bitmap = !args.getSession().isDownloaded() ? null : 
											ImageHelper.fromFile(args.getSession().getFile(), expectedWidth, expectedHeight);
										ImageEventArgs iea = new ImageEventArgs(
												fullUri, bitmap, args.getSession().getError());
										IEvent<ImageEventArgs> evt = allImageHandlers.get(fullUri);
										evt.fireEvent(null, iea);
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

	@Override
	public void bind(String fullUri, ViewGroup viewGroup) {
		Logger.check(viewGroup != null, "Parameter viewGroup cannot be null !");
		if (viewGroup instanceof AdapterView)
			bind(fullUri, (BaseAdapter)((AdapterView<?>)viewGroup).getAdapter());
		else if (viewGroup instanceof IPlainAdapterView)
			bind(fullUri, (BaseAdapter)((IPlainAdapterView)viewGroup).getAdapter());
		else 
			Logger.check(false, "ViewGroup must be a AdapterView or IPlainAdapterView!");
	}
	class AdapterRefreshHandler implements IEventHandler<ImageEventArgs>{
		WeakReference<BaseAdapter> refAdapter; 
		public AdapterRefreshHandler(BaseAdapter adapter){
			refAdapter = new WeakReference<BaseAdapter>(adapter);
		}
		public BaseAdapter getAdapter(){
			return refAdapter.get();
		}
		@Override
		public void process(Object sender, ImageEventArgs args) {
			if (refAdapter.get() != null && !refAdapter.get().isEmpty())
				refAdapter.get().notifyDataSetChanged();
		}

	}
	List<AdapterRefreshHandler> adapterHandlers = new ArrayList<AdapterRefreshHandler>();
	@Override
	public void bind(String fullUri, BaseAdapter adapter) {
		Logger.check(adapter != null, "Parameter adapter cannot be null !");
		Bitmap bitmap = loadBitmap(fullUri);
		if (bitmap == null){
			AdapterRefreshHandler handler = null;
			Iterator<AdapterRefreshHandler> it = adapterHandlers.iterator();
			while(it.hasNext()){
				AdapterRefreshHandler h = it.next();
				if (h.getAdapter() == null) 
					it.remove();
				else if (h.getAdapter() == adapter){
					handler = h;
					break;
				}				
			}
			if (handler == null){
				handler = new AdapterRefreshHandler(adapter);
				adapterHandlers.add(handler);
			}
			addImageEventHandler(fullUri, handler);
		}
	}

	@Override
	public void start() {	
		downloader.start();
	}

	@Override
	public void stop() {
		allImageHandlers.clear();
		imageViewHandlers.clear();
		adapterHandlers.clear();
		downloader.stop();
	}
}
