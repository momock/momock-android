package com.momock.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.os.AsyncTask;

public class ImageHelper {
	public static Bitmap fromFile(final String fn) {
		return fromFile(fn, -1, -1);
	}

	public static Bitmap fromFile(final String fn, final int expectedWidth,
			final int expectedHeight) {
		File f = new File(fn);
		return fromFile(f, expectedWidth, expectedHeight);
	}

	public static Bitmap fromFile(final File f) {
		return fromFile(f, -1, -1);
	}

	public static Bitmap fromFile(final File f, final int expectedWidth,
			final int expectedHeight) {
		if (f == null)
			return null;
		try {
			if (expectedWidth > 0 && expectedHeight > 0) {
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
					options.inSampleSize = Math.max(inWidth / expectedWidth,
							inHeight / expectedHeight);
					roughBitmap = BitmapFactory.decodeStream(in, null, options);
				} finally {
					in.close();
				}
				float[] values = new float[9];
				Matrix m = new Matrix();
				RectF inRect = new RectF(0, 0, roughBitmap.getWidth(),
						roughBitmap.getHeight());
				RectF outRect = new RectF(0, 0, expectedWidth, expectedHeight);
				m.setRectToRect(inRect, outRect, Matrix.ScaleToFit.CENTER);
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
			Logger.error(e.getMessage());
		}
		return null;
	}

	public static Bitmap fromStream(final InputStream in) {
		return fromStream(in, -1, -1);
	}

	public static Bitmap fromStream(final InputStream in,
			final int expectedWidth, final int expectedHeight) {
		if (in == null)
			return null;
		if (expectedWidth > 0 && expectedHeight > 0) {
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
				Logger.error(e.getMessage());
			}
		} else {
			return BitmapFactory.decodeStream(in);
		}
		return null;
	}

	public interface ImageDownloadCallback {
		void onImageDownloading(int percent);

		void onImageDownloaded(File imageFile);
	}

	static class ImageDownloader extends AsyncTask<String, Integer, String> {

		ImageDownloadCallback callback;
		File imageFile = null;

		ImageDownloader(ImageDownloadCallback callback) {
			this.callback = callback;
		}

		@Override
		protected String doInBackground(String... sUrl) {
			try {
				URL url = new URL(sUrl[0]);
				URLConnection connection = url.openConnection();
				connection.connect();
				int fileLength = connection.getContentLength();

				InputStream input = new BufferedInputStream(url.openStream());
				imageFile = File.createTempFile("image", "tmp");
				OutputStream output = new FileOutputStream(imageFile);

				byte data[] = new byte[1024];
				long total = 0;
				int count;
				while ((count = input.read(data)) != -1) {
					total += count;
					publishProgress((int) (total * 100 / fileLength));
					output.write(data, 0, count);
				}

				output.flush();
				output.close();
				input.close();
			} catch (Exception e) {
			}
			return null;
		}

		@Override
		protected void onProgressUpdate(Integer... progress) {
			super.onProgressUpdate(progress);
			callback.onImageDownloading(progress[0]);
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			callback.onImageDownloaded(imageFile);
		}
	}
}
