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

import com.momock.util.Logger;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

public abstract class CaseActivity extends FragmentActivity {

	protected abstract String getCaseName();
	protected ICase<FragmentActivity> kase = null;

	@SuppressWarnings("unchecked")
	public ICase<FragmentActivity> getCase() {
		if (kase == null) {
			kase = (ICase<FragmentActivity>)App.get().getCase(getCaseName());
		}
		return kase;
	}

	protected void log(String msg){
		Logger.debug((getCase() == null ? getClass().getName() : getCase().getFullName()) + " : " + msg);
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		log("onCreate");
		super.onCreate(savedInstanceState);
		getCase().attach(this);
	}

	@Override
	protected void onStart() {
		log("onStart");
		super.onStart();
		App.get().setActiveCase(getCase());
	}

	@Override
	protected void onStop() {
		log("onStop");
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		log("onDestroy");
		super.onDestroy();
		getCase().detach();
	}

	@Override
	public void onLowMemory() {
		log("onLowMemory");
		super.onLowMemory();
	}

	@Override
	protected void onPause() {
		log("onPause");
		super.onPause();
	}

	@Override
	protected void onResume() {
		log("onResume");
		super.onResume();
	}

	@Override
	protected void onPostResume() {
		log("onPostResume");
		super.onPostResume();
	}

	@Override
	protected void onResumeFragments() {
		log("onResumeFragments");
		super.onResumeFragments();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		log("onSaveInstanceState");
		super.onSaveInstanceState(outState);
	}

	@Override
	public void onAttachFragment(Fragment fragment) {
		log("onAttachFragment");
		super.onAttachFragment(fragment);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		log("onRestoreInstanceState");
		super.onRestoreInstanceState(savedInstanceState);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		log("onPostCreate");
		super.onPostCreate(savedInstanceState);
	}

	@Override
	protected void onRestart() {
		log("onRestart");
		super.onRestart();
	}
}
