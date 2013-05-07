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
import java.io.FileOutputStream;
import java.io.RandomAccessFile;
import java.util.List;
import java.util.UUID;

import javax.inject.Inject;

import android.app.ActivityManager;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.telephony.TelephonyManager;

import com.momock.app.App;
import com.momock.app.IApplication;
import com.momock.util.FileHelper;
import com.momock.util.Logger;

public class SystemService implements ISystemService {
	@Inject
	IApplication app;

	@Inject
	TelephonyManager telephonyManager;

	@Inject
	ConnectivityManager connectivityManager = null;

	@Inject
	ActivityManager activityManager;

	@Override
	public void openUrl(String url) {

		app.getCurrentContext().startActivity(
				new Intent(Intent.ACTION_VIEW, Uri.parse(url)));

	}

	@Override
	public Class<?>[] getDependencyServices() {
		return null;
	}

	String imsi = null;
	String imei = null;
	String mcc = null;
	String mnc = null;

	@Override
	public void start() {
		try {
			imsi = telephonyManager.getSubscriberId();
			if (imsi != null && imsi.length() == 16)
				imsi = imsi.substring(1);
			if (imsi != null && imsi.length() != 15)
				imsi = null;
			imei = telephonyManager.getDeviceId();

			if ((telephonyManager.getNetworkOperator() != null)
					&& (((telephonyManager.getNetworkOperator().length() == 5) || (telephonyManager
							.getNetworkOperator().length() == 6)))) {
				mcc = telephonyManager.getNetworkOperator().substring(0, 3);
				mnc = telephonyManager.getNetworkOperator().substring(3);
			}
		} catch (Exception e) {
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
		intent.setDataAndType(Uri.fromFile(file),
				"application/vnd.android.package-archive");
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
		return mcc;
	}

	@Override
	public String getMnc() {
		return mnc;
	}

	@Override
	public boolean isNetworkAvailable() {
		if (connectivityManager == null)
			return true;
		return connectivityManager.getActiveNetworkInfo() != null
				&& connectivityManager.getActiveNetworkInfo()
						.isConnectedOrConnecting();
	}

	@Override
	public void killProcess(String packageName) {
		activityManager.killBackgroundProcesses(packageName);
	}

	@Override
	public boolean isProcessRunning(String packageName) {
		if (packageName == null)
			return false;
		List<ActivityManager.RunningAppProcessInfo> list = activityManager
				.getRunningAppProcesses();
		if (list != null) {
			for (int i = 0; i < list.size(); ++i) {
				if (packageName.equals(list.get(i).processName))
					return true;
			}
		}
		return false;
	}

	String deviceId = null;
	private static final String DEVICE_ID_FILE_NAME = "generated.device.id";

	@Override
	public String getDeviceId() {
		if (deviceId != null)
			return deviceId;
		File idfile = new File(App.get().getFilesDir(), DEVICE_ID_FILE_NAME);
		try {
			if (!idfile.exists()) {
				deviceId = UUID.randomUUID().toString();
				FileHelper.writeTextFile(idfile, deviceId);
			} else {
				deviceId = FileHelper.readTextFile(idfile);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return deviceId;
	}
}
