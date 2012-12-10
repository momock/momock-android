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

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.ContentHandler;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.google.android.filecache.FileResponseCache;
import com.google.android.imageloader.BlockingFilterInputStream;
import com.google.android.imageloader.ContentURLStreamHandlerFactory;
import com.google.android.imageloader.ImageLoader;
import com.momock.app.App;
import com.momock.event.Event;
import com.momock.event.IEvent;
import com.momock.event.IEventHandler;
import com.momock.holder.ImageHolder;
import com.momock.util.Convert;
import com.momock.util.ImageHelper;
import com.momock.util.Logger;
import com.momock.widget.IPlainAdapterView;

public class ImageService extends ImageLoader implements IImageService {
	static class ImageContentHandler extends ContentHandler {
		@Override
		public Bitmap getContent(URLConnection connection) throws IOException {
			InputStream input = null;
			try {
				input = new BlockingFilterInputStream(connection.getInputStream());
				Bitmap bitmap = BitmapFactory.decodeStream(input);
				if (bitmap == null) {
					throw new IOException("Image could not be decoded");
				}
				return bitmap;
			} catch(IOException e){
				Logger.error(e.getMessage());
				throw e;
			} finally {
				if (input != null)
					input.close();
			}
		}
	}

	public ImageService() {
		super(DEFAULT_TASK_LIMIT, new ContentURLStreamHandlerFactory(App.get()
				.getContentResolver()), new ImageContentHandler(),
				FileResponseCache.sink(), DEFAULT_CACHE_SIZE, null);
	}

	Map<String, IEvent<ImageEventArgs>> allImageHandlers = new HashMap<String, IEvent<ImageEventArgs>>();

	@Override
	public void addImageEventHandler(String url,
			IEventHandler<ImageEventArgs> handler) {
		IEvent<ImageEventArgs> evt;
		if (allImageHandlers.containsKey(url)) {
			evt = allImageHandlers.get(url);
		} else {
			evt = new Event<ImageEventArgs>();
			allImageHandlers.put(url, evt);
		}
		evt.addEventHandler(handler);
	}

	@Override
	public void removeImageEventHandler(String url,
			IEventHandler<ImageEventArgs> handler) {
		IEvent<ImageEventArgs> evt;
		if (allImageHandlers.containsKey(url)) {
			evt = allImageHandlers.get(url);
			evt.removeEventHandler(handler);
		}
	}

	@Override
	public Bitmap loadBitmap(String uri) {
		return loadBitmap(uri, true);
	}

	@Override
	public Bitmap loadBitmap(final String uri, boolean highPriority) {
		final int expectedWidth;
		final int expectedHeight;
		int pos = uri.lastIndexOf('#');
		if (pos > 0) {
			int pos2 = uri.lastIndexOf('x');
			Logger.check(pos2 > pos, "The image uri is not correct!");
			expectedWidth = Convert.toInteger(uri.substring(pos + 1, pos2 - pos
					- 1));
			expectedHeight = Convert.toInteger(uri.substring(pos2 + 1));
		} else {
			expectedWidth = -1;
			expectedHeight = -1;
		}
		Bitmap bitmap = null;
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
			bitmap = getBitmap(uri);
			ImageError error = getError(uri);
			if (bitmap == null && error == null) {

		        Iterator<?> it = mRequests.iterator();
		        while (it.hasNext()) {
		        	ImageRequest r = (ImageRequest)it.next();
		            if (r.getUrl().equals(uri)) {
		                it.remove();
		                Logger.debug("Found ImageRequest of " + uri + " in the queue, remove it.");
		            }
		        }
		        
				ImageRequest request = new ImageRequest(uri,
						new ImageCallback() {

							@Override
							public boolean unwanted() {
								return false;
							}

							@Override
							public void send(String url, Bitmap bitmap,
									ImageError error) {
								IEvent<ImageEventArgs> evt;
								if (allImageHandlers.containsKey(url)) {
									ImageEventArgs args = new ImageEventArgs(
											uri, bitmap, error == null ? null
													: error.getCause());
									evt = allImageHandlers.get(url);
									evt.fireEvent(null, args);
								}
							}

						}, true);
				if (highPriority)
					insertRequestAtFrontOfQueue(request);
				else
					enqueueRequest(request);
			}
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
	public void bind(String fullUri, ImageView view) {
		Logger.check(view != null, "Parameter view cannot be null !");
		Bitmap bitmap = getBitmap(fullUri);
		if (bitmap != null)
			view.setImageBitmap(bitmap);
		else {
			loadBitmap(fullUri);
			final WeakReference<ImageView> refView = new WeakReference<ImageView>(
					view);
			addImageEventHandler(fullUri, new IEventHandler<ImageEventArgs>() {

				@Override
				public void process(Object sender, ImageEventArgs args) {
					if (refView.get() != null)
						refView.get().setImageBitmap(args.getBitmap());
				}

			});
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
}
