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
package com.momock.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.RectF;

public class ImageHelper {

	public static boolean toFile(Bitmap bitmap, File file) {
		if (file == null) return false;
		try {
			FileOutputStream out = new FileOutputStream(file);
			Bitmap.CompressFormat format = Bitmap.CompressFormat.PNG;
			String fn = file.getName().toLowerCase();
			if (fn.endsWith(".jpg") || fn.endsWith(".jpeg"))
				format = Bitmap.CompressFormat.JPEG;
			bitmap.compress(format, 85, out);
			out.close();
		} catch (Exception e) {
			Logger.error(e);
			return false;
		}
		return true;
	}

	public static boolean toFile(Bitmap bitmap, String filename) {
		if (filename == null) return false;		
		return toFile(bitmap, new File(filename));
	}

	public static Bitmap fromFile(final String fn) {
		return fromFile(fn, 0, 0);
	}

	public static Bitmap fromFile(final String fn, final int expectedWidth,
			final int expectedHeight) {
		if (fn == null) return null;
		File f = new File(fn);
		return fromFile(f, expectedWidth, expectedHeight);
	}

	public static Bitmap fromFile(final File f) {
		return fromFile(f, 0, 0);
	}

	public static Bitmap fromFile(final File f, final int expectedWidth,
			final int expectedHeight) {
		if (f == null)
			return null;
		try {
			if (expectedWidth != 0 && expectedHeight != 0) {
				int inWidth;
				int inHeight;
				InputStream in = new FileInputStream(f);
				BitmapFactory.Options options = new BitmapFactory.Options();
				try {
					options.inJustDecodeBounds = true;
					BitmapFactory.decodeStream(in, null, options);
				} finally {
					in.close();
				}
				inWidth = options.outWidth;
				inHeight = options.outHeight;
				final Bitmap roughBitmap;
				in = new FileInputStream(f);
				try {
					options = new BitmapFactory.Options();
					if (expectedWidth > 0)
						options.inSampleSize = Math.max(inWidth / expectedWidth, inHeight / expectedHeight);
					else
						options.inSampleSize = Math.min(-100 / expectedWidth, -100 / expectedHeight);
					options.inPreferredConfig = Bitmap.Config.RGB_565;
					roughBitmap = BitmapFactory.decodeStream(in, null, options);
				} finally {
					in.close();
				}
				if (roughBitmap == null) return null;
				float[] values = new float[9];
				Matrix m = new Matrix();
				RectF inRect = new RectF(0, 0, roughBitmap.getWidth(), roughBitmap.getHeight());
				RectF outRect = expectedWidth > 0 ? 
						new RectF(0, 0, expectedWidth, expectedHeight) :
						new RectF(0, 0, -roughBitmap.getWidth() * expectedWidth / 100, -roughBitmap.getHeight() * expectedHeight / 100);
				m.setRectToRect(inRect, outRect, expectedWidth > 0 ? Matrix.ScaleToFit.CENTER : Matrix.ScaleToFit.FILL);
				m.getValues(values);
				final Bitmap resizedBitmap = Bitmap.createScaledBitmap(
						roughBitmap,
						(int) (roughBitmap.getWidth() * values[0]),
						(int) (roughBitmap.getHeight() * values[4]), true);
				return resizedBitmap;
			} else {
				InputStream in = new FileInputStream(f);
				try {
					return BitmapFactory.decodeStream(in);
				} finally {
					in.close();
				}
			}
		} catch (IOException e) {
			Logger.error(e);
		}
		return null;
	}

	public static Bitmap fromStream(final InputStream in) {
		return fromStream(in, 0, 0);
	}

	public static Bitmap fromStream(final InputStream in,
			final int expectedWidth, final int expectedHeight) {
		if (in == null)
			return null;
		if (expectedWidth != 0 && expectedHeight != 0) {
			File tempFile;
			try {
				tempFile = File.createTempFile("image", ".tmp");
				final FileOutputStream tempOut = new FileOutputStream(tempFile);
				int len;
				byte[] bs = new byte[1024 * 10];
				while ((len = in.read(bs)) > 0) {
					tempOut.write(bs, 0, len);
				}
				tempOut.close();
				Bitmap bitmap = fromFile(tempFile, expectedWidth,
						expectedHeight);
				tempFile.delete();
				return bitmap;
			} catch (IOException e) {
				Logger.error(e);
			}
		} else {
			return BitmapFactory.decodeStream(in);
		}
		return null;
	}
}
