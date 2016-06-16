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
package com.momock.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Patterns;
import android.view.Display;
import android.view.WindowManager;

public class SystemHelper {
	public static class PhoneInfo{
		private String imei;
		private String imsi;
		private String mcc;
		private String mnc;
		public PhoneInfo(String imei, String imsi, String mcc, String mnc){
			this.imei = imei;
			this.imsi = imsi;
			this.mcc = mcc;
			this.mnc = mnc;
		}
		public String getImei(){
			return imei;
		}
		public String getImsi(){
			return imsi;
		}
		public String getMcc(){
			return mcc;
		}
		public String getMnc(){
			return mnc;
		}
	}
	public static String getAndroidId(Context context){
		return Secure.getString(context.getContentResolver(),Secure.ANDROID_ID);
	}
	public static String getWifiMac(Context context){
		try {
			WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
			if (wm.getConnectionInfo() == null) return null;
			return wm.getConnectionInfo().getMacAddress();
		} catch (Exception e) {
			Logger.error(e);
			return null;
		}
	}
	public static String getBluetoothMac(Context context){
		try {
			if (BluetoothAdapter.getDefaultAdapter() == null) return null;
			return BluetoothAdapter.getDefaultAdapter().getAddress();
		} catch (Exception e) {
			Logger.error(e);
			return null;
		}
	}

	@SuppressLint("DefaultLocale")
	public static String getMd5(String text) {
		MessageDigest m = null;
		try {
			m = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			Logger.error(e);
		}
		m.update(text.getBytes());
		byte md5Data[] = m.digest();

		String uniqueID = new String();
		for (int i = 0; i < md5Data.length; i++) {
			int b = (0xFF & md5Data[i]);
			if (b <= 0xF)
				uniqueID += "0";
			uniqueID += Integer.toHexString(b);
		}
		return uniqueID.toUpperCase();
	}
	public static PhoneInfo getPhoneInfo(Context context){
		String imsi = null, imei = null, mcc = null, mnc = null;		
		TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		try {
			imsi = tm.getSubscriberId();
			if (imsi != null && imsi.length() == 16)
				imsi = imsi.substring(1);
			if (imsi != null && imsi.length() != 15)
				imsi = null;
			imei = tm.getDeviceId();
			String op = tm.getNetworkOperator();
			if ((op != null) && (((op.length() == 5) || (op.length() == 6)))) {
				mcc = tm.getNetworkOperator().substring(0, 3);
				mnc = tm.getNetworkOperator().substring(3);
			}
		} catch (Exception e) {
			Logger.error(e);
		}
		return new PhoneInfo(imei, imsi, mcc, mnc);
	}
	public static String getOsVersion(){
		return android.os.Build.VERSION.RELEASE;
	}
	public static String getAppId(Context context){
		return context.getPackageName();
	}
	public static String getAppVersion(Context context) {
		PackageManager pm = context.getPackageManager();	
		PackageInfo pInfo;
		try {
			pInfo = pm.getPackageInfo(getAppId(context), 0);
			return pInfo.versionName;
		} catch (NameNotFoundException e) {
			Logger.error(e);
		}
		return "0";
	}
	public static String getUA(boolean withAndroidVersion){		
		String ua = "android";	
		if (withAndroidVersion)
			ua += ";VERSION/" + android.os.Build.VERSION.RELEASE;
		ua += ";MANUFACTURER/" + android.os.Build.MANUFACTURER;
		ua += ";MODEL/" + android.os.Build.MODEL;
		ua += ";BOARD/" + android.os.Build.BOARD;
		ua += ";BRAND/" + android.os.Build.BRAND;
		ua += ";DEVICE/" + android.os.Build.DEVICE;
		ua += ";HARDWARE/" + android.os.Build.HARDWARE;
		ua += ";PRODUCT/" + android.os.Build.PRODUCT;
		return ua;
	}
	public static Location getLastLocation(Context context){
		Location loc = null;
		try{
			LocationManager lm  = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
			loc = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
			if (loc == null)
				loc = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		} catch(Exception e) {
			Logger.error(e);
		}
		return loc;
	}
	public static boolean isSystemApp(Context context){
		PackageManager pm = context.getPackageManager();	
		try {
			ApplicationInfo ai = pm.getApplicationInfo(context.getPackageName(), 0);
			return (ai.flags & ApplicationInfo.FLAG_SYSTEM) != 0;
		} catch (NameNotFoundException e) {
			Logger.error(e);
		}
		return false;
	}
	public static boolean isSystemApp(Context context, String packageName){
		PackageManager pm = context.getPackageManager();	
		try {
			ApplicationInfo ai = pm.getApplicationInfo(packageName, 0);
			return (ai.flags & ApplicationInfo.FLAG_SYSTEM) != 0;
		} catch (NameNotFoundException e) {
			Logger.error(e);
		}
		return false;
	}
	public static String getCountry(Context context){
		String country = Locale.getDefault().getCountry();
		String c = null;
		Logger.info("Locale Country : " + country);
		TelephonyManager tm = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
		c = tm.getSimCountryIso();
		Logger.info("Sim Country : " + c);
		if (c != null && c.length() == 2)
			country = c;
		c = tm.getNetworkCountryIso();
		Logger.info("Net Country : " + country);	
		if (c != null && c.length() == 2)
			country = c;	
		
		try{
			LocationManager lm = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
		    Criteria criteria = new Criteria();
		    String provider = lm.getBestProvider(criteria, false);
		    Location loc = lm.getLastKnownLocation(provider);
		    if (loc != null){
			    Geocoder gc = new Geocoder(context, Locale.ENGLISH);
			    List<Address> addrs = gc.getFromLocation(loc.getLatitude(), loc.getLongitude(), 1);
			    if (addrs != null && addrs.size() == 1){
			    	c = addrs.get(0).getCountryCode();
			    	Logger.info("GPS Country : " + c);
					if (c != null && c.length() == 2)
						country = c;	
			    }
		    }
		} catch(Exception e) {
			//Logger.error(e);
		}
		
		country = country.toLowerCase();
		Logger.info("Country : " + country);
		return country;
	}
	static int sw = 0;
	static int sh = 0;
	static void initScreenSize(Context context){
		if (sw == 0){
			WindowManager wm = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
			Display display = wm.getDefaultDisplay();
			DisplayMetrics metrics = new DisplayMetrics();
			display.getMetrics(metrics);		
			sw = metrics.widthPixels;
			sh = metrics.heightPixels;
		}
	}
	public static String getScreen(Context context){
		initScreenSize(context);
		return sw + "x" + sh;
	}

	public static int getScreenWidth(Context context){
		initScreenSize(context);
		return sw;
	}

	public static int getScreenHeight(Context context){
		initScreenSize(context);
		return sh;
	}
	
	public static void openLink(Context context, String url){
		if (url == null) 
			url = "http://google.com";
		else if (url.indexOf("://") == -1)
			url = "http://" + url;
		Logger.debug("Open Link :" + url);
		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_VIEW);
		intent.setData(Uri.parse(url));
		context.startActivity(intent);
	}
	
	public static boolean isInstalled(Context context, String id){
        PackageManager pm = context.getPackageManager();
        try {
        	pm.getPackageInfo(id, PackageManager.GET_ACTIVITIES);
        	return true;
        } catch (Exception e) {
        	//Logger.error(e);
        }
        return false;
	}

	public static boolean isTablet(Context context) {
		boolean xlarge = ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_XLARGE);
		boolean large = ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE);
		return (xlarge || large);
	}
	
	public static String getPackageNameFromUrl(String url){
		if (url == null) return null;
		String prefix = "details?id=";
		int pos = url.indexOf(prefix);
		if (pos > 0){
			return url.substring(pos + prefix.length());
		}
		return null;
	}
	
	public static PackageInfo[] getInstalledApps(Context context){
		PackageManager pkgManager = context.getPackageManager();
		List<PackageInfo> packs = pkgManager.getInstalledPackages(0);
		List<PackageInfo> appList = new ArrayList<PackageInfo>();
		for (int i = 0; i < packs.size(); i++) {
			PackageInfo p = packs.get(i);
			Intent intent = pkgManager.getLaunchIntentForPackage(p.packageName);
			if (intent != null){
				appList.add(p);
			}
		}
		PackageInfo[] apps = new PackageInfo[appList.size()];
		appList.toArray(apps);
		Arrays.sort(apps, new Comparator<PackageInfo>(){

			@Override
			public int compare(PackageInfo lhs, PackageInfo rhs) {
				return lhs.packageName.compareToIgnoreCase(rhs.packageName);
			}
			
		});
		return apps;
	}
	public static List<String> getEmailAccounts(Context context){
		List<String> emails = new ArrayList<String>();
		try{
			Pattern emailPattern = Patterns.EMAIL_ADDRESS; 
			Account[] accounts = AccountManager.get(context).getAccounts();
			for (Account account : accounts) {
			    if (emailPattern.matcher(account.name).matches()) {
			    	emails.add(account.name);		        
			    }
			}
		}catch(Exception e){
			Logger.error(e);
		}
		return emails;
	}
	
	public static boolean isRoaming(Context context)
	{
		TelephonyManager tm = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
		return tm.isNetworkRoaming();
	}
	
	public static String getMarketUrl(String packageName){
		return "market://details?id=" + packageName;
	}
	
	public static boolean hasSdcard(Context context){
		return android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
	}
}
