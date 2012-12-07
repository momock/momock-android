package com.momock.service;

import static android.os.Environment.getExternalStorageDirectory;
import static android.os.Environment.getExternalStorageState;

import java.io.File;
import java.net.ResponseCache;
import java.net.URI;
import java.util.List;
import java.util.Map;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Environment;

import com.google.android.filecache.FileResponseCache;
import com.momock.app.App;

public class CacheService implements ICacheService{
	File cacheDir;
	class RemoteResponseCache extends FileResponseCache{
		static final String CACHE_DIR_NAME = "remote"; 
		public RemoteResponseCache(){
			Context context = App.get();
			
			if (getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
				cacheDir = new File(
						Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO ? getExternalCacheDir(context)
								: new File(getExternalStorageDirectory().getPath() + "/Android/data/"
										+ context.getPackageName() + "/cache/"), CACHE_DIR_NAME);
			} else {
				cacheDir = new File(context.getCacheDir(), CACHE_DIR_NAME);
			}
			if (cacheDir != null && !cacheDir.exists()) {
				cacheDir.mkdirs();
			}
		}

		@TargetApi(Build.VERSION_CODES.FROYO)
		public File getExternalCacheDir(final Context context) {
			return context.getExternalCacheDir();
		}
		
		@Override
		protected File getFile(URI uri, String requestMethod,
				Map<String, List<String>> requestHeaders, Object cookie) {
			return getCacheOf(uri.toString());
		}

		public void clear() {
			if (cacheDir == null) return;
			final File[] files = cacheDir.listFiles();
			if (files == null) return;
			for (final File f : files) {
				f.delete();
			}
		}
	}
	public CacheService(){
		ResponseCache.setDefault(new RemoteResponseCache());
	}
	String getFilenameOf(String uri){
		return uri.replaceFirst("https?:\\/\\/", "").replaceAll("[^a-zA-Z0-9]", "_");
	}
	@Override
	public File getCacheDir() {
		return cacheDir;
	}

	@Override
	public File getCacheOf(String uri) {
		return new File(cacheDir, getFilenameOf(uri));
	}

}
