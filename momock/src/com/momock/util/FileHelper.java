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

import static android.os.Environment.getExternalStorageDirectory;
import static android.os.Environment.getExternalStorageState;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Environment;

public class FileHelper {
	public static void writeText(File file, String text) throws IOException{
		writeText(file, text, null);
	}
	public static void writeText(File file, String text, String encoding) throws IOException{
		if (file.exists()) file.delete();
		if (text == null) return;
		try {
			FileOutputStream fos = new FileOutputStream(file);
			fos.write(text.getBytes(encoding == null ? "UTF-8" : encoding));
			fos.close();
		} catch (FileNotFoundException e) {
			Logger.error(e);
		}
	}
	public static String readText(File source) throws IOException{
		return readText(source, null);
	}
	public static String readText(File source, String encoding) throws IOException{
		if (source == null || !source.exists()) return null;
		StringBuilder sb = new StringBuilder();
		FileInputStream fis = new FileInputStream(source);
		InputStreamReader isr = new InputStreamReader(fis, encoding == null ? "UTF-8" : encoding);
		char[] cs = new char[10240];
		int len;
		while((len = isr.read(cs)) > 0){
			sb.append(cs, 0, len);
		}
		isr.close();
		fis.close();
		return sb.toString();
	}
	public static String readText(InputStream source) throws IOException{
		return readText(source, null);
	}
	public static String readText(InputStream source, String encoding) throws IOException{
		StringBuilder sb = new StringBuilder();
		InputStreamReader isr = new InputStreamReader(source, encoding == null ? "UTF-8" : encoding);
		char[] cs = new char[10240];
		int len;
		while((len = isr.read(cs)) > 0){
			sb.append(cs, 0, len);
		}
		isr.close();
		return sb.toString();
	}
	public static void copy(File source, File target) throws IOException{
		FileInputStream fis = new FileInputStream(source);
		FileOutputStream fos = new FileOutputStream(target);
		copy(fis, fos);
		fis.close();
		fos.close();
	}

	public static void copy(InputStream is, String file) {
		copy(is, new File(file));
	}
	public static void copy(InputStream is, File file) {
		try {
			FileOutputStream fos = new FileOutputStream(file);
			copy(is, fos);
			fos.close();
		} catch (Exception e) {
			Logger.error(e);
		}
	}
	public static void copy(InputStream is, OutputStream os) {
		if (is == null || os == null) return ;
		try {
			byte[] bs = new byte[1024];
			int len;
			while((len = is.read(bs)) > 0){
				os.write(bs, 0, len);
			}
		} catch (Exception e) {
			Logger.error(e);
		}
	}
	public static String getFilenameOf(String uri) {
		return uri.replaceFirst("https?:\\/\\/", "").replaceAll("[^a-zA-Z0-9.]",
				"_");
	}

	@TargetApi(Build.VERSION_CODES.FROYO)
	static File getExternalCacheDir(final Context context) {
		return context.getExternalCacheDir();
	}
	static File cacheDir = null;
	public static File getCacheDir(Context context, String category) {
		if (cacheDir == null){
			if (getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
				cacheDir = Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO ? 
						getExternalCacheDir(context)
						: new File(getExternalStorageDirectory().getPath() + "/Android/data/" + context.getPackageName() + "/cache/");
			} else {
				cacheDir = context.getCacheDir();
			}
			if (cacheDir != null && !cacheDir.exists()) {
				cacheDir.mkdirs();
			}
		}
		File fc = category == null ? cacheDir : new File(cacheDir, category);
		if (!fc.exists())
			fc.mkdir();
		return fc;
	}

	public static File getCacheOf(Context context, String category, String uri) {
		return new File(getCacheDir(context, category), getFilenameOf(uri));
	}
	public static File getFileInCard(String path){
		if (getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) 
			return new File(getExternalStorageDirectory().getPath() + path);
		return null;
	}
	public static void mkdirForFile(File f){
		mkdir(f.getParentFile());
	}
	public static void mkdir(File dir){
		try{
			if (dir == null) return;
			if (!dir.exists()){
				mkdir(dir.getParentFile());
				dir.mkdir();
			}
		}catch(Exception e){
			Logger.error(e);
		}
	}
	public static boolean unzip(String zipFile, String outputFolder) {
		try {
			return unzip(new FileInputStream(zipFile), new File(outputFolder));
		} catch (FileNotFoundException e) {
			Logger.error(e);
			return false;
		}
	}
	public static boolean unzip(InputStream is, File folder) {
		if (is == null || folder == null) return false;
		byte[] buffer = new byte[1024];

		try {
			if (!folder.exists()) {
				folder.mkdir();
			}
			ZipInputStream zis = new ZipInputStream(is);
			ZipEntry ze = zis.getNextEntry();

			while (ze != null) {

				String fileName = ze.getName();
				File newFile = new File(folder, fileName);

				Logger.debug("file unzip : " + newFile.getAbsoluteFile());

				newFile.getParentFile().mkdir();

				if (ze.isDirectory()){
					newFile.mkdir();
				} else {
					FileOutputStream fos = new FileOutputStream(newFile);

					int len;
					while ((len = zis.read(buffer)) > 0) {
						fos.write(buffer, 0, len);
					}

					fos.close();
				}
				ze = zis.getNextEntry();
			}

			zis.closeEntry();
			zis.close();

			Logger.debug("Done");
			return true;
		} catch (Exception e) {
			Logger.error(e);
			return false;
		}
	}
	
	public static void delete(File file) {  
	    if (file.isFile()) {  
	        file.delete();  
	        return;  
	    }  

	    if(file.isDirectory()){  
	        File[] childFiles = file.listFiles();  
	        if (childFiles == null || childFiles.length == 0) {  
	            file.delete();  
	            return;  
	        }  
	  
	        for (int i = 0; i < childFiles.length; i++) {  
	        	delete(childFiles[i]);  
	        }  
	        file.delete();  
	    }  
	} 
}
