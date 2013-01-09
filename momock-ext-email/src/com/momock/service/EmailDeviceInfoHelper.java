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

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import android.content.Context;
import android.telephony.TelephonyManager;

public class EmailDeviceInfoHelper {
	static String getLocalIpAddressInfo() {
		StringBuilder sb = new StringBuilder();
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface
					.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf
						.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress()) {
						sb.append(inetAddress.getHostAddress().toString() + "\r\n");
					}
				}
			}
		} catch (SocketException ex) {
		}
		return sb.toString();
	}
	public static String getFullMessage(Context context, String msg){
		StringBuilder sb = new StringBuilder();
		TelephonyManager mTelephonyMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		sb.append("IMSI :" + mTelephonyMgr.getSubscriberId() + "\r\n");
		sb.append("IMEI :" + mTelephonyMgr.getDeviceId() + "\r\n");
		sb.append("ID :" + android.os.Build.ID + "\r\n");
		sb.append("USER :" + android.os.Build.USER + "\r\n");
		sb.append("IP :" + getLocalIpAddressInfo() + "\r\n");		
		String ua = "android";
		ua += ";VERSION/" + android.os.Build.VERSION.RELEASE;
		ua += ";MANUFACTURER/" + android.os.Build.MANUFACTURER;
		ua += ";MODEL/" + android.os.Build.MODEL;
		ua += ";BOARD/" + android.os.Build.BOARD;
		ua += ";BRAND/" + android.os.Build.BRAND;
		ua += ";DEVICE/" + android.os.Build.DEVICE;
		ua += ";HARDWARE/" + android.os.Build.HARDWARE;
		ua += ";PRODUCT/" + android.os.Build.PRODUCT;
		sb.append("UA :" + ua + "\r\n");
		sb.append("\r\n======================================================\r\n");
		sb.append(msg);
		return sb.toString();
	}
}
