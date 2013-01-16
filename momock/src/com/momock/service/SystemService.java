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

import javax.inject.Inject;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.telephony.TelephonyManager;

import com.momock.app.IApplication;
import com.momock.util.Logger;

public class SystemService implements ISystemService {
	@Inject 
	IApplication app;
	
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

}
