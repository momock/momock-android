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
package com.momock.app;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.momock.service.ILayoutInflaterService;
import com.momock.util.Logger;
import com.momock.util.MemoryHelper;
import com.momock.util.ViewHelper;

public abstract class CaseActivity extends Activity {

	protected abstract String getCaseName();
	protected abstract void onCreate();
	
	protected ICase<Activity> kase = null;
	
	@SuppressWarnings("unchecked")
	public ICase<Activity> getCase() {
		if (kase == null) {
			kase = (ICase<Activity>)App.get().findChildCase(getCaseName());
		}
		Logger.check(kase != null, getCaseName() + " has not been created!");
		return kase;
	}

	protected void log(String msg){
		Logger.info("*" + (getCase() == null ? getClass().getName() : getCase().getFullName()) + "(" + Integer.toHexString(this.hashCode()) +") : " + msg);
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		log("onCreate");
		App.get().onCreateActivity();
		super.onCreate(savedInstanceState);
		onCreate();
		getCase().attach(CaseActivity.this);
	}

	@Override
	protected void onStart() {
		log("onStart");
		super.onStart();
		App.get().setCurrentActivity(this);
		App.get().setActiveCase(getCase());
	}

	@Override
	protected void onStop() {
		int usedMem = (int)(MemoryHelper.getAvailableMemory() / 1024);		
		log("onStop : " + usedMem + "K");
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		int usedMemBegin = (int)(MemoryHelper.getAvailableMemory() / 1024);		
		super.onDestroy();
		getCase().detach();
		App.get().onDestroyActivity();
		ViewGroup contentFrame = (ViewGroup) findViewById(android.R.id.content);
		ViewHelper.clean(contentFrame);
		System.gc();
		int usedMemEnd = (int)(MemoryHelper.getAvailableMemory() / 1024);
		log("onDestroy : " + usedMemBegin + "K -> " + usedMemEnd + "K");
	}

	@Override
	public void onLowMemory() {
		int usedMem = (int)(MemoryHelper.getAvailableMemory() / 1024);	
		log("onLowMemory : " + usedMem + "K");
		super.onLowMemory();
		App.get().onLowMemory();
	}

	@Override
	protected void onPause() {
		int usedMemBegin = (int)(MemoryHelper.getAvailableMemory() / 1024);
		super.onPause();
		getCase().onHide();
		int usedMemEnd = (int)(MemoryHelper.getAvailableMemory() / 1024);
		log("onPause : " + usedMemBegin + "K -> " + usedMemEnd + "K");
	}

	@Override
	protected void onResume() {
		if (App.get().getCurrentActivity() != this){
			App.get().setCurrentActivity(this);
			log("restore current activity");
		}
		if (!this.getCase().isActive()){
			App.get().setActiveCase(getCase());
			log("restore active case");
		}
		int usedMemBegin = (int)(MemoryHelper.getAvailableMemory() / 1024);
	    System.gc();
		super.onResume();
		getCase().onShow();
		int usedMemEnd = (int)(MemoryHelper.getAvailableMemory() / 1024);
		log("onResume : " + usedMemBegin + "K -> " + usedMemEnd + "K");
		App.get().checkMemory();

		if (App.get().isExiting()){
			log("Finishing");
			finish();
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		int usedMem = (int)(MemoryHelper.getAvailableMemory() / 1024);	
		log("onSaveInstanceState : " + usedMem + "K");
		getCase().onSaveState(outState);
		super.onSaveInstanceState(outState);
	}

	@Override
	public void onAttachFragment(Fragment fragment) {
		int usedMem = (int)(MemoryHelper.getAvailableMemory() / 1024);	
		log("onAttachFragment : " + usedMem + "K");
		super.onAttachFragment(fragment);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		log("onRestoreInstanceState");
		getCase().onRestoreState(savedInstanceState);
		super.onRestoreInstanceState(savedInstanceState);
	}

	@Override
	protected void onRestart() {
		int usedMem = (int)(MemoryHelper.getAvailableMemory() / 1024);	
		log("onRestart : " + usedMem + "K");
		super.onRestart();
	}

	@Override
	public Object getSystemService(String name) {
		if (getCase() == null){
			super.getSystemService(name);
		}
		Object service = super.getSystemService(name);
		if (service instanceof LayoutInflater){
			ILayoutInflaterService layoutInflaterService = getCase().getService(ILayoutInflaterService.class);
			if (layoutInflaterService != null)
				return layoutInflaterService.getLayoutInflater((LayoutInflater)service);
		}
		return service;
	}
	
	@Override
	public LayoutInflater getLayoutInflater() {
		if (getCase() == null){
			return super.getLayoutInflater();
		}
		ILayoutInflaterService layoutInflaterService = getCase().getService(ILayoutInflaterService.class);
		return layoutInflaterService.getLayoutInflater(this);
	}
	@Override
	public void onBackPressed() {
		if (!getCase().onBack())
			super.onBackPressed();
	}

}
