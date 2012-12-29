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

import java.lang.ref.WeakReference;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;

import com.momock.app.App;
import com.momock.event.Event;
import com.momock.event.EventArgs;
import com.momock.event.IEvent;
import com.momock.event.IEventHandler;
import com.momock.service.IImageService;
import com.momock.service.IImageService.ImageEventArgs;

public abstract class ImageHolder implements IHolder{
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
	public BitmapDrawable getAsDrawable() {
		return new BitmapDrawable(App.get().getResources(), getAsBitmap());
	}
	public abstract Bitmap getAsBitmap();

	public static ImageHolder get(final int id) {
		final String uri = "android.resource://" + App.get().getPackageName() + "/" + id;
		return new ImageHolder() {

			@Override
			public String getUri() {
				return uri;
			}
			
			@Override
			public BitmapDrawable getAsDrawable() {
				return (BitmapDrawable)App.get().getResources().getDrawable(id);
			}

			@Override
			public Bitmap getAsBitmap() {
				BitmapDrawable drawable = getAsDrawable();
				return drawable == null ? null : drawable.getBitmap();
			}

		};
	}
	public static ImageHolder get(final String uri){
		return get(uri, -1, -1);
	}
	public static ImageHolder get(final String uri, final int expectedWidth, final int expectedHeight){
		final IImageService is = App.get().getImageService();
		final String fullUri = is.getFullUri(uri, expectedWidth, expectedHeight);		
		ImageHolder holder = new ImageHolder() {	
			WeakReference<Bitmap> refBitmap = null;
			@Override
			public String getUri() {
				return uri;
			}
			@Override
			public Bitmap getAsBitmap() {
				if (refBitmap == null || refBitmap.get() == null){
					final ImageHolder self = this;	
					refBitmap = new WeakReference<Bitmap>(is.loadBitmap(fullUri));
					if (refBitmap.get() == null && is.isRemote(fullUri)){
						IEventHandler<ImageEventArgs> handler = new IEventHandler<ImageEventArgs>(){
							@Override
							public void process(Object sender, ImageEventArgs args) {
								refBitmap = new WeakReference<Bitmap>(args.getBitmap());
								getImageLoadedEvent().fireEvent(self, new EventArgs());
							}							
						};
						is.addImageEventHandler(fullUri, handler);
					}
				}
				return refBitmap.get();
			}
		};		
		return holder;
	}
}
