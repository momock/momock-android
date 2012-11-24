package com.momock.holo.app;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import com.momock.app.App;
import com.momock.app.ICase;
import com.momock.util.Logger;

public abstract class CaseActivity extends ActionBarFragmentActivity {

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
		Logger.info((getCase() == null ? getClass().getName() : getCase().getFullName()) + " : " + msg);
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
	protected void onRestart() {
		log("onRestart");
		super.onRestart();
	}
}
