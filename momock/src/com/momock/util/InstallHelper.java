/*******************************************************************************
 * Copyright 2015 momock.com
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

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class InstallHelper {
	static interface OnInstalledPackaged {
		
		public void packageInstalled(String packageName, int returnCode);

	}

	public interface ICallback {
		void onResult(boolean success);
	}
	public static void install(Context context, String file){
		Intent intentInstall = new Intent(Intent.ACTION_VIEW);
		intentInstall.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intentInstall.setDataAndType(Uri.parse(file), "application/vnd.android.package-archive");
		context.startActivity(intentInstall);		
	}
	public static void install(Context context, File file){
		Intent intentInstall = new Intent(Intent.ACTION_VIEW);
		intentInstall.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intentInstall.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
		context.startActivity(intentInstall);
	}
	public static boolean isApkValid(File file){
		try{

			ZipInputStream zis = new ZipInputStream(new FileInputStream(file));			
			ZipEntry ze = zis.getNextEntry();

			while (ze != null) {
				String fileName = ze.getName();
				Logger.debug(fileName);
				ze = zis.getNextEntry();
			}

			zis.closeEntry();
			zis.close();

		} catch(Exception e) {
			Logger.error(e);
		}
		return false;
	}
	public static boolean directInstall(Context context, String file){
		return directInstall(context, new File(file));
	}
	public static boolean directInstall(Context context, File file){
	
		Process p = null;
		DataOutputStream os = null;
		DataInputStream is = null;
		try {
			String dest = "/data/app/" + file.getName();
			String cmd = " cat " + file.getAbsolutePath() + " > " + dest;
			Logger.debug("Install with cat : " + cmd);
			p = Runtime.getRuntime().exec("su");
			os = new DataOutputStream(p.getOutputStream());
			is = new DataInputStream(p.getInputStream());
			/*
			os.writeBytes(cmd + " \n");   
			os.writeBytes("chmod a+rw " + dest + " || busybox chmod a+rw " + dest + " \n");
			*/
			os.writeBytes("pm install " + file.getAbsolutePath() + " \n");
			//os.writeBytes("ls -l " + dest + " \n");
            os.writeBytes("exit \n");
            os.flush();
			int r = p.waitFor();
			Logger.debug("su result : " + r);
			if (r == 0){
				Logger.debug("Installed : " + file);
				return true;
			}
				
			/*
			Logger.debug(FileHelper.readText(is));
			if (new File(dest).exists()){
				Logger.debug("Installed : " + dest);
			}*/
		} catch (Exception e) {
			Logger.error(e);
		} finally {
			try {
				if (os != null) {
					os.close();
				}
				if (is != null) {
					is.close();
				}
				if (p != null)
					p.destroy();
			} catch (Exception e) {
				Logger.error(e);
			}
		}
		return false;
	
	}
}
