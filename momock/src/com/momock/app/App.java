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
import java.util.HashMap;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.view.View;

import com.momock.outlet.IOutlet;
import com.momock.outlet.PlaceholderOutlet;
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
		Object ao = App.get().getActiveCase().getAttachedHandle();
		if (ao == null)
			return null;
		if (ao instanceof Context)
			return (Context) ao;
		if (ao instanceof View)
			return ((View) ao).getContext();
		if (ao instanceof Fragment)
			return ((Fragment) ao).getActivity();
		return null;
	}

	public void addCase(String name, Class<?> kaseClass) {
		Class<?>[] parmTypes = { ICase.class};
		Object[] parms = { this.getRootCase() };
		try {
			Constructor<?> constructor = kaseClass.getConstructor(parmTypes);
			addCase(name, (ICase) constructor.newInstance(parms));
		} catch (Exception e) {
			Logger.error(e.getMessage());
		}
	}

	public void runCase(String name) {
		getCase(name).run();
	}

	public void startActivity(Class<?> activityClass) {
		Context currContext = getCurrentContext();
		currContext.startActivity(new Intent(currContext, activityClass));
	}

	// Implementation for IApplication interface
	protected ICase activeCase = null;
	protected ICase rootCase = null;
	protected HashMap<String, ICase> cases = new HashMap<String, ICase>();

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
	public ICase getCase(String name) {
		if (name == null)
			return null;
		return cases.get(name);
	}

	@Override
	public void addCase(String name, ICase kase) {
		if (!cases.containsKey(name))
			cases.put(name, kase);
	}

	@Override
	public void removeCase(String name) {
		if (cases.containsKey(name))
			cases.remove(name);
	}

	@SuppressWarnings("rawtypes")
	HashMap<String, IOutlet> outlets = new HashMap<String, IOutlet>(); 
	@SuppressWarnings("rawtypes")
	@Override
	public IOutlet getOutlet(String name) {
		IOutlet outlet = null;
		if (outlets.containsKey(name))
			outlet = outlets.get(name);
		else
		{
			outlet = new PlaceholderOutlet();
			outlets.put(name, outlet);
		}
		return outlet;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void addOutlet(String name, IOutlet outlet) {
		if (outlets.containsKey(name) && outlet != null)
		{
			IOutlet oldOutlet = outlets.get(name);
			if (oldOutlet instanceof PlaceholderOutlet)
				((PlaceholderOutlet)oldOutlet).transfer(outlet);
		}
		if (outlet == null)
			outlets.remove(name);
		else
			outlets.put(name, outlet);
	}

	@Override
	public void removeOutlet(String name) {
		if (outlets.containsKey(name))
		{
			outlets.remove(name);
		}
	}
}
