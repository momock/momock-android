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
package com.momock.holder;

import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import com.momock.app.App;
import com.momock.event.Event;
import com.momock.event.EventArgs;
import com.momock.event.IEvent;
import com.momock.event.IEventHandler;
import com.momock.service.IImageService;
import com.momock.service.IImageService.ImageEventArgs;

public class ImageHolder{
	protected IEvent<EventArgs> imageLoadedEvent = null;
	public ImageHolder(){
		onCreate();
	}
	protected void onCreate(){
		
	}
	public boolean isLoaded(){
		return getAsBitmap() != null;
	}
	protected IEvent<EventArgs> getImageLoadedEvent(){
		if (imageLoadedEvent == null)
			imageLoadedEvent = new Event<EventArgs>();
		return imageLoadedEvent;
	}
	public void addImageLoadedEventHandler(IEventHandler<EventArgs> handler){
		getImageLoadedEvent().addEventHandler(handler);
	}
	public void removeImageLoadedEventHandler(IEventHandler<EventArgs> handler){
		getImageLoadedEvent().removeEventHandler(handler);
	}
	public String getUri(){
		return null;
	}
	BitmapDrawable drawable = null;
	public BitmapDrawable getAsDrawable() {
		if (drawable == null)
			drawable = new BitmapDrawable(App.get().getResources(), getAsBitmap());
		return drawable;
	}
	Bitmap bitmap = null;
	public Bitmap getAsBitmap() {
		if (bitmap == null)
		{
			Drawable drawable = getAsDrawable();
			if (drawable instanceof BitmapDrawable) {
				return ((BitmapDrawable) drawable).getBitmap();
			}
			bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
					drawable.getIntrinsicHeight(), Config.ARGB_8888);
			Canvas canvas = new Canvas(bitmap);
			drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
			drawable.draw(canvas);
		}
		return bitmap;
	}

	static final Map<String, SoftReference<ImageHolder>> imageCache = new HashMap<String, SoftReference<ImageHolder>>(); 
	public static ImageHolder get(final int id) {
		final String uri = "android.resource://" + App.get().getPackageName() + "/" + id;
		return new ImageHolder() {

			@Override
			public String getUri() {
				return uri;
			}
			
			@Override
			public BitmapDrawable getAsDrawable() {
				if (drawable == null)
					drawable = (BitmapDrawable)App.get().getResources().getDrawable(id);
				return drawable;
			}

		};
	}
	public static ImageHolder get(final String uri){
		return get(uri, -1, -1, true);
	}
	public static ImageHolder get(final String uri, final int expectedWidth, final int expectedHeight){
		return get(uri, expectedWidth, expectedHeight, true);
	}
	public static ImageHolder get(final String uri, boolean highPriority){
		return get(uri, -1, -1, highPriority);
	}
	public static ImageHolder get(final String uri, final int expectedWidth, final int expectedHeight, final boolean highPriority){
		final IImageService is = App.get().getImageService();
		final String fullUri = is.getFullUri(uri, expectedWidth, expectedHeight);
		if (imageCache.containsKey(fullUri)){
			SoftReference<ImageHolder> ih = imageCache.get(fullUri);
			if (ih.get() != null)
				return ih.get();
			else
				imageCache.remove(fullUri);
		}
		ImageHolder holder = new ImageHolder() {		
			@Override
			protected void onCreate(){
				final ImageHolder self = this;
				bitmap = is.loadBitmap(is.getFullUri(uri, expectedWidth, expectedHeight), highPriority);
				if (bitmap == null && is.isRemote(uri)){
					is.addImageEventHandler(fullUri, new IEventHandler<ImageEventArgs>(){

						@Override
						public void process(Object sender, ImageEventArgs args) {
							bitmap = args.getBitmap();
							getImageLoadedEvent().fireEvent(self, new EventArgs());
						}
						
					});
				}
			}
			@Override
			public String getUri() {
				return uri;
			}
			@Override
			public Bitmap getAsBitmap() {
				return bitmap;
			}
		};		
		imageCache.put(fullUri, new SoftReference<ImageHolder>(holder));
		return holder;
	}
	public static ImageHolder get(final Bitmap bitmap){
		return new ImageHolder() {

			@Override
			public Bitmap getAsBitmap() {
				return bitmap;
			}
			
		};
	}
	
	public static ImageHolder get(final BitmapDrawable drawable){
		return new ImageHolder() {

			@Override
			public BitmapDrawable getAsDrawable() {
				return drawable;
			}
			
		};
	}
}
