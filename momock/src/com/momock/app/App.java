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

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.momock.util.Logger;

public class App extends android.app.Application implements IApplication {
	static App app = null;

	public static App get() {
		return app;
	}

	protected int getLogLevel() {
		return Logger.LEVEL_DEBUG;
	}

	@Override
	public void onCreate() {
		Logger.open(this.getClass().getName().toLowerCase() + ".log",
				getLogLevel());
		app = this;
		super.onCreate();
		onAddCases();
		onAddServices();
	}

	@Override
	public void onTerminate() {
		super.onTerminate();
		Logger.close();
	}

	protected void onAddCases() {

	}

	protected void onAddServices() {

	}

	// Helper methods
	public Context getCurrentContext() {
		Object ao = App.get().getActiveCase().getAttachedObject();
		if (ao == null)
			return null;
		if (ao instanceof Context)
			return (Context) ao;
		if (ao instanceof View)
			return ((View) ao).getContext();
		return null;
	}

	public void addCase(Class<?> kaseClass) {
		addCase(kaseClass.getName(), kaseClass);
	}

	public void addCase(String name, Class<?> kaseClass) {
		Class<?>[] parmTypes = { ICase.class, String.class };
		Object[] parms = { this.getRootCase(), name };
		try {
			Constructor<?> constructor = kaseClass.getConstructor(parmTypes);
			addCase((ICase) constructor.newInstance(parms));
		} catch (Exception e) {
			Logger.error(e.getMessage());
		}
	}

	public ICase getCase(Class<?> kaseClass) {
		return this.getCaseByName(kaseClass.getName());
	}

	public void runCase(Class<?> kaseClass) {
		getCase(kaseClass).run();
	}

	public void startActivity(Class<?> activityClass) {
		Context currContext = getCurrentContext();
		currContext.startActivity(new Intent(currContext, activityClass));
	}

	// Implementation for IApplication interface
	protected ICase activeCase = null;
	protected ICase rootCase = null;
	protected List<ICase> cases = new ArrayList<ICase>();

	@Override
	public ICase getActiveCase() {
		return activeCase;
	}

	@Override
	public void setActiveCase(ICase kase) {
		if (activeCase != kase) {
			if (activeCase != null)
				activeCase.onDeactivate();
			activeCase = kase;
			if (activeCase != null)
				activeCase.onActivate();
		}
	}

	@Override
	public ICase getRootCase() {
		if (rootCase == null)
			rootCase = new RootCase(this);
		return rootCase;
	}

	@Override
	public ICase getCaseByName(String name) {
		if (name == null)
			return null;
		for (int i = 0; i < cases.size(); i++) {
			ICase kase = cases.get(i);
			if (name.equals(kase.getName()))
				return kase;
		}
		return null;
	}

	@Override
	public void addCase(ICase kase) {
		if (!cases.contains(kase))
			cases.add(kase);
	}

	@Override
	public void removeCase(ICase kase) {
		if (cases.contains(kase))
			cases.remove(kase);
	}
}
