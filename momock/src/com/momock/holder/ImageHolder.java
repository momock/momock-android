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
import java.lang.ref.WeakReference;
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
import com.momock.service.IImageService;
import com.momock.service.IImageService.ImageSetter;
import com.momock.util.Convert;
import com.momock.util.ImageHelper;

public class ImageHolder{
	public static final String PREFIX_ID = "id://";
	public static final String PREFIX_FILE = "file://";
	public static final String PREFIX_RES = "res://";
	public static final String PREFIX_HTTP = "http://";
	public static final String PREFIX_HTTPS = "https://";
	
	public static class ImageLoadEventArgs extends EventArgs{
		
	}
	protected IEvent<ImageLoadEventArgs> imageLoadEvent = null;
	public ImageHolder(){
		onCreate();
	}
	protected void onCreate(){
		
	}
	public boolean isLoaded(){
		return getAsBitmap() != null;
	}
	public IEvent<ImageLoadEventArgs> getImageLoadEvent(){
		if (imageLoadEvent == null)
			imageLoadEvent = new Event<ImageLoadEventArgs>();
		return imageLoadEvent;
	}
	public String getUri(){
		return null;
	}
	Drawable drawable = null;
	public Drawable getAsDrawable() {
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
		//return get(PREFIX_ID + id);
		return get("android.resource://" + App.get().getPackageName() + "/" + id);
	}
	public static ImageHolder get(final String uri){
		return get(uri, -1, -1);
	}
	public static ImageHolder get(final String uri, final int expectedWidth, final int expectedHeight){
		final IImageService is = App.get().getImageService();
		if (imageCache.containsKey(uri)){
			SoftReference<ImageHolder> ih = imageCache.get(uri);
			if (ih.get() != null)
				return ih.get();
			else
				imageCache.remove(uri);
		}
		ImageHolder holder = new ImageHolder() {		
			WeakReference<Bitmap> refBitmap = null;		

			@Override
			protected void onCreate(){
				final ImageHolder self = this;
				is.load(this, new ImageSetter(){
					@Override
					public void setImage(Bitmap bitmap) {
						refBitmap = new WeakReference<Bitmap>(bitmap);
						ImageLoadEventArgs args = new ImageLoadEventArgs();
						getImageLoadEvent().fireEvent(self, args);
					}						
				});
			}
			@Override
			public String getUri() {
				return uri;
			}
			@Override
			public boolean isLoaded(){
				return refBitmap != null;
			}
			@Override
			public Bitmap getAsBitmap() {
				return refBitmap == null ? null : refBitmap.get();
			}
		};		
		imageCache.put(uri, new SoftReference<ImageHolder>(holder));
		return holder;
	}
	public static ImageHolder get1(final String uri, final int expectedWidth, final int expectedHeight){
		if (imageCache.containsKey(uri)){
			SoftReference<ImageHolder> ih = imageCache.get(uri);
			if (ih.get() != null)
				return ih.get();
			else
				imageCache.remove(uri);
		}
		if (uri.startsWith(PREFIX_ID)){
			final int id = Convert.toInteger(uri.substring(PREFIX_ID.length()));
			return new ImageHolder() {

				@Override
				public String getUri() {
					return uri;
				}
				
				@Override
				public Drawable getAsDrawable() {
					if (drawable == null)
						drawable = App.get().getResources().getDrawable(id);
					return drawable;
				}

			};
		} else if (uri.startsWith(PREFIX_FILE)) {
			return new ImageHolder() {				
				@Override
				public String getUri() {
					return uri;
				}

				@Override
				public Bitmap getAsBitmap() {
					if (bitmap == null)
						bitmap = ImageHelper.fromFile(uri.substring(PREFIX_FILE.length()), expectedWidth, expectedHeight);
					return bitmap;
				}
			};
		} else if (uri.startsWith(PREFIX_RES)) {
			return new ImageHolder() {				
				@Override
				public String getUri() {
					return uri;
				}

				@Override
				public Bitmap getAsBitmap() {
					if (bitmap == null)
						bitmap = ImageHelper.fromStream(ImageHolder.class.getResourceAsStream(uri.substring(PREFIX_RES.length())), expectedWidth, expectedHeight);
					return bitmap;
				}
			};			
		} else {
			return new ImageHolder() {				
				@Override
				public String getUri() {
					return uri;
				}

				@Override
				public Bitmap getAsBitmap() {
					if (bitmap == null)
						bitmap = ImageHelper.fromStream(ImageHolder.class.getResourceAsStream(uri.substring(PREFIX_RES.length())), expectedWidth, expectedHeight);
					return bitmap;
				}
			};		
		}
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
			public Drawable getAsDrawable() {
				return drawable;
			}
			
		};
	}
}
