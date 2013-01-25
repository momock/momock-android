/*******************************************************************************
 * Copyright 2013 momock.com
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

import java.io.File;
import java.util.List;

import javax.inject.Inject;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.telephony.TelephonyManager;

import com.momock.app.IApplication;
import com.momock.util.Logger;

public class SystemService implements ISystemService {
	@Inject 
	IApplication app;

	@Inject
	ConnectivityManager connectivityManager = null;

	@Inject 
	ActivityManager activityManager;
	@Override
	public void openUrl(String url) {

		app.getCurrentContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));

	}

	@Override
	public Class<?>[] getDependencyServices() {
		return null;
	}
	String imsi = null;
	String imei = null;
	@Override
	public void start() {
		try{
			TelephonyManager tm = (TelephonyManager) app.getCurrentContext().getSystemService(Context.TELEPHONY_SERVICE);
			imsi = tm.getSubscriberId();
			if (imsi != null && imsi.length() == 16)
				imsi = imsi.substring(1);
			if (imsi != null && imsi.length() != 15)
				imsi = null;
			imei = tm.getDeviceId();
		}catch(Exception e){
			Logger.error(e);
		}
	}

	@Override
	public void stop() {
		
	}

	@Override
	public void install(File file) {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
		app.getCurrentContext().startActivity(intent);
	}

	@Override
	public boolean canStop() {
		return true;
	}

	@Override
	public String getImsi() {
		return imsi;
	}

	@Override
	public String getImei() {
		return imei;
	}

	@Override
	public String getMcc() {
		if (imsi == null) return null;
		return imsi.substring(0, 3);
	}

	@Override
	public boolean isNetworkAvailable() {
		if (connectivityManager == null) return true;
		return connectivityManager.getActiveNetworkInfo() != null && 
				connectivityManager.getActiveNetworkInfo().isConnectedOrConnecting();
	}
	@Override
	public void exit(){
		//android.os.Process.killProcess(android.os.Process.myPid());
		System.exit(0);
	}
	@Override
	public void killProcess(String packageName) {
		activityManager.killBackgroundProcesses(packageName);
	}
	@Override
	public boolean isProcessRunning(String packageName){
		if (packageName == null) return false;
		List<ActivityManager.RunningAppProcessInfo> list = activityManager.getRunningAppProcesses();
		if (list != null) {
			for (int i = 0; i < list.size(); ++i) {
				if (packageName.equals(list.get(i).processName)) 
					return true;
			}
		}
		return false;
	}
}
