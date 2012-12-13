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

import static android.os.Environment.getExternalStorageDirectory;
import static android.os.Environment.getExternalStorageState;

import java.io.File;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Environment;

import com.momock.app.App;

public class CacheService implements ICacheService{
	File cacheDir;
	static final String CACHE_DIR_NAME = "filecaches"; 
	public CacheService(){
	}
	String getFilenameOf(String uri){
		return uri.replaceFirst("https?:\\/\\/", "").replaceAll("[^a-zA-Z0-9]", "_");
	}
	@Override
	public File getCacheDir(String category) {
		File fc = category == null ? cacheDir : new File(cacheDir, category);
		if (!fc.exists()) fc.mkdir();
		return fc;
	}

	@Override
	public File getCacheOf(String category, String uri) {
		return new File(getCacheDir(category), getFilenameOf(uri));
	}

	@TargetApi(Build.VERSION_CODES.FROYO)
	File getExternalCacheDir(final Context context) {
		return context.getExternalCacheDir();
	}


	public void clear() {
		if (cacheDir == null) return;
		final File[] files = cacheDir.listFiles();
		if (files == null) return;
		for (final File f : files) {
			f.delete();
		}
	}
	
	@Override
	public void start() {
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
	@Override
	public void stop() {		
	}

}
