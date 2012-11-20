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

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import com.momock.app.App;

public class ImageHolder implements IGraphicsHolder{
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

	public static ImageHolder get(final int id) {
		return new ImageHolder() {
			@Override
			public Drawable getAsDrawable() {
				if (drawable == null)
					drawable = App.get().getResources().getDrawable(id);
				return drawable;
			}
		};
	}
}
