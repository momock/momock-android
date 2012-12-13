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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.momock.app.App;
import com.momock.holder.ImageHolder;

public class TextHelper {
	public static final String PREFIX_FILE = "file://";
	public static final String PREFIX_RES = "res://";
	public static final String PREFIX_ASSETS = "assets://";

	public static String read(InputStream is) {
		return read(is, "UTF-8");
	}
	
	public static String read(InputStream is, String encoding) {
		int buflen = 1024 * 10;
		int len;
		InputStreamReader isr = new InputStreamReader(is);
		char[] buffer = new char[buflen];
		try {
			StringBuffer sb = new StringBuffer();
			while ((len = isr.read(buffer)) > 0) {
				sb.append(buffer, 0, len);
			}
			return sb.length() > 0 ? sb.toString() : null;
		} catch (IOException e) {
			Logger.error(e.getMessage());
		}
		return null;
	}

	public static String read(String uri){
		return read(uri, "UTF-8");
	}
	
	public static String read(String uri, String encoding) {
		if (uri.startsWith(PREFIX_FILE)) {
			File f = new File(uri.substring(PREFIX_FILE.length()));
			FileInputStream fis;
			try {
				fis = new FileInputStream(f);
			} catch (FileNotFoundException e) {
				Logger.error(e.getMessage());
				return null;
			}
			return read(fis, encoding);
		} else if (uri.startsWith(PREFIX_RES)) {
			return read(ImageHolder.class.getResourceAsStream(uri
					.substring(PREFIX_RES.length())), encoding);
		} else if (uri.startsWith(PREFIX_ASSETS)) {
			try {
				return read(
						App.get().getResources().getAssets()
								.open(uri.substring(PREFIX_ASSETS.length())),
						encoding);
			} catch (IOException e) {
				Logger.error(e.getMessage());
				return null;
			}
		}
		return null;
	}
}
