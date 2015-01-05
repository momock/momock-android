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
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.momock.util.Logger;
import com.momock.util.MemoryHelper;
import com.momock.util.ViewHelper;

public class CaseFragment extends Fragment{
	protected IActiveCaseIndicator activeCaseIndicator = null;
	protected ICase<Fragment> kase = null;

	protected String getCaseName(){
		throw new RuntimeException("Case has not been assigned.");
	}
	protected IApplication getApplication(){
		return App.get();
	}
	@SuppressWarnings("unchecked")
	public ICase<Fragment> getCase() {
		if (kase == null) {
			String name = getCaseName();			
			kase = name == null ? null : (ICase<Fragment>)getApplication().getCase(name);
		}
		Logger.check(kase != null, getCaseName() + " has not been created!");
		return kase;
	}

	public void setCase(ICase<Fragment> kase){
		this.kase = kase;
	}
	protected void log(String msg){
		Logger.info((getCase() == null ? this.toString() : getCase().getFullName()) + "(" + Integer.toHexString(this.hashCode()) +") : " + msg);
	}
	
	View contentFrame = null;
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		log("onViewCreated");
		contentFrame = view;
		super.onViewCreated(view, savedInstanceState);
		if (getCase() != null)
			getCase().attach(this);
	}
	@Override
	public void onDestroyView() {
		int usedMemBegin = (int)(MemoryHelper.getAvailableMemory() / 1024);
		super.onDestroyView();
		if (getCase() != null)
			getCase().detach();
		ViewHelper.clean(contentFrame);
		contentFrame = null;
		System.gc();
		int usedMemEnd = (int)(MemoryHelper.getAvailableMemory() / 1024);
		log("onDestroyView : " + usedMemBegin + "K -> " + usedMemEnd + "K");
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		log("onActivityResult");
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onAttach(Activity activity) {
		log("onAttach");
		super.onAttach(activity);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		log("onCreate");
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		log("onCreateView");
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		log("onActivityCreated");
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onStart() {
		log("onStart");
		super.onStart();
		if (getCase().getParent() != null && (getActiveCaseIndicator() == null || getActiveCaseIndicator() != null && getActiveCaseIndicator().isActiveCase()))
			getCase().getParent().setActiveCase(getCase());
	}

	@Override
	public void onResume() {
		int usedMemBegin = (int)(MemoryHelper.getAvailableMemory() / 1024);
	    System.gc();
		super.onResume();
		getCase().onShow();
		int usedMemEnd = (int)(MemoryHelper.getAvailableMemory() / 1024);
		log("onResume : " + usedMemBegin + "K -> " + usedMemEnd + "K");
		App.get().checkMemory();

		if (App.get().isExiting()){
			log("Finishing");
			getActivity().finish();
		}
	}

	@Override
	public void onPause() {
		int usedMemBegin = (int)(MemoryHelper.getAvailableMemory() / 1024);		
		super.onPause();
		getCase().onHide();
		int usedMemEnd = (int)(MemoryHelper.getAvailableMemory() / 1024);
		log("onPause : " + usedMemBegin + "K -> " + usedMemEnd + "K");
	}

	@Override
	public void onStop() {
		int usedMem = (int)(MemoryHelper.getAvailableMemory() / 1024);		
		log("onStop : " + usedMem + "K");
		super.onStop();
	}

	@Override
	public void onLowMemory() {
		int usedMem = (int)(MemoryHelper.getAvailableMemory() / 1024);		
		log("onLowMemory : " + usedMem + "K");
		super.onLowMemory();
	}


	@Override
	public void onDestroy() {
		int usedMem = (int)(MemoryHelper.getAvailableMemory() / 1024);		
		log("onDestroy : " + usedMem + "K");
		super.onDestroy();
	}

	@Override
	public void onDetach() {
		int usedMem = (int)(MemoryHelper.getAvailableMemory() / 1024);	
		log("onDetach : " + usedMem + "K");
		super.onDetach();
	}
	@Override
	public void onSaveInstanceState(Bundle outState) {
		int usedMem = (int)(MemoryHelper.getAvailableMemory() / 1024);	
		log("onSaveInstanceState : " + usedMem + "K");
		getCase().onSaveState(outState);
		super.onSaveInstanceState(outState);
	}
	public IActiveCaseIndicator getActiveCaseIndicator() {
		return activeCaseIndicator;
	}
	public void setActiveCaseIndicator(IActiveCaseIndicator activeCaseIndicator) {
		this.activeCaseIndicator = activeCaseIndicator;
	}
}
