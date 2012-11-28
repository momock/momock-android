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

import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import com.momock.data.DataSet;
import com.momock.data.IDataSet;
import com.momock.outlet.IOutlet;
import com.momock.outlet.IPlug;
import com.momock.outlet.PlaceholderOutlet;
import com.momock.service.IImageService;
import com.momock.service.IService;
import com.momock.service.ImageService;
import com.momock.util.Logger;

public abstract class App extends android.app.Application implements
		IApplication {
	public class CustomLayoutInflater extends android.view.LayoutInflater
			implements Cloneable {

		public CustomLayoutInflater(LayoutInflater original, Context newContext) {
			super(original, newContext);
		}

		protected CustomLayoutInflater(Context context) {
			super(context);
		}

		@Override
		public LayoutInflater cloneInContext(Context newContext) {
			return new CustomLayoutInflater(this, newContext);
		}

		@Override
		protected View onCreateView(String name, AttributeSet attrs)
				throws ClassNotFoundException {
			if (shortNames.containsKey(name)) {
				try {
					return createView(name, shortNames.get(name) + ".", attrs);
				} catch (Exception e) {
					Logger.error(e.getMessage());
				}
			}
			try {
				return createView(name, "android.widget.", attrs);
			} catch (ClassNotFoundException e) {
				return createView(name, "android.view.", attrs);
			}
		}

		@TargetApi(Build.VERSION_CODES.HONEYCOMB)
		@Override
		protected View onCreateView(View parent, String name, AttributeSet attrs)
				throws ClassNotFoundException {
			if (shortNames.containsKey(name)) {
				try {
					return createView(name, shortNames.get(name) + ".", attrs);
				} catch (Exception e) {
					Logger.error(e.getMessage());
				}
			}
			try {
				return createView(name, "android.widget.", attrs);
			} catch (ClassNotFoundException e) {
				return createView(name, "android.view.", attrs);
			}
		}
	}

	static App app = null;

	Map<Context, LayoutInflater> cachedLayoutInflater = new WeakHashMap<Context, LayoutInflater>();

	public static App get() {
		return app;
	}

	public String buildCaseFullName(Object... names) {
		String fullname = "";
		for (int i = 0; i < names.length; i++) {
			String name = null;
			Object n = names[i];
			Logger.check(n instanceof String || n instanceof Class<?>,
					"Only String or Class are allows to build full case name!");
			if (n instanceof CharSequence)
				name = n.toString();
			else if (n instanceof Class<?>)
				name = ((Class<?>) n).getName();
			fullname += "/" + name;
		}
		return fullname;
	}

	public ICase<?> getCase(Object... names) {
		ICase<?> kase = null;
		for (int i = 0; i < names.length; i++) {
			String name = null;
			Object n = names[i];
			Logger.check(n instanceof String || n instanceof Class<?>,
					"Only String or Class are allows to build full case name!");
			if (n instanceof CharSequence)
				name = n.toString();
			else if (n instanceof Class<?>)
				name = ((Class<?>) n).getName();
			if (i == 0)
				kase = getCase(name);
			else {
				if (kase == null)
					return null;
				else
					kase = kase.getCase(name);
			}
		}
		return kase;
	}

	protected int getLogLevel() {
		return Logger.LEVEL_DEBUG;
	}

	public LayoutInflater getLayoutInflater(Context context) {
		if (cachedLayoutInflater.containsKey(context))
			return cachedLayoutInflater.get(context);
		LayoutInflater layoutInflater = new CustomLayoutInflater(
				LayoutInflater.from(context), context);
		cachedLayoutInflater.put(context, layoutInflater);
		return layoutInflater;
	}

	public LayoutInflater getLayoutInflater(LayoutInflater inflater) {
		if (!(inflater instanceof CustomLayoutInflater))
			inflater = new CustomLayoutInflater(inflater, inflater.getContext());
		return inflater;
	}

	@Override
	public void onCreate() {
		Logger.open(this.getClass().getName().toLowerCase() + ".log",
				getLogLevel());
		app = this;
		super.onCreate();
	}

	@Override
	public void onTerminate() {
		super.onTerminate();
		Logger.close();
	}

	protected abstract void onAddCases();

	protected abstract void onAddServices();

	protected void onRegisterShortNames() {
		registerShortName("android.support.v4.view", "ViewPager",
				"PagerTitleStrip");
		registerShortName("android.webkit", "WebView");
	}

	// Helper methods
	public Activity getCurrentActivity() {
		Object ao = App.get().getActiveCase().getAttachedObject();
		if (ao == null)
			return null;
		if (ao instanceof Activity)
			return (Activity) ao;
		if (ao instanceof View)
			return (Activity) ((View) ao).getContext();
		if (ao instanceof Fragment)
			return ((Fragment) ao).getActivity();
		return null;
	}

	public void runCase(String name) {
		getCase(name).run();
	}

	public void startActivity(Class<?> activityClass) {
		Context currContext = getCurrentActivity();
		currContext.startActivity(new Intent(currContext, activityClass));
	}

	// Implementation for IApplication interface
	protected ICase<?> activeCase = null;
	protected HashMap<String, ICase<?>> cases = new HashMap<String, ICase<?>>();

	@Override
	public ICase<?> getActiveCase() {
		return activeCase;
	}

	@Override
	public void setActiveCase(ICase<?> kase) {
		if (activeCase != kase) {
			if (activeCase != null)
				activeCase.onDeactivate();
			activeCase = kase;
			if (activeCase != null)
				activeCase.onActivate();
		}
	}

	@Override
	public ICase<?> getCase(String name) {
		Logger.check(name != null, "Parameter name cannot be null!");
		ICase<?> kase = null;
		int pos = name.indexOf('/');
		if (pos == -1) {
			kase = cases.get(name);
		} else {
			if (name.startsWith("/"))
				name = name.substring(1);
			pos = name.indexOf('/');
			if (pos == -1)
				kase = cases.get(name);
			else {
				kase = cases.get(name.substring(0, pos));
				if (kase != null)
					kase = kase.getCase(name.substring(pos + 1));
			}
		}
		return kase;
	}

	@Override
	public void addCase(ICase<?> kase) {
		if (!cases.containsKey(kase.getName())) {
			cases.put(kase.getName(), kase);
			kase.onCreate();
		}
	}

	@Override
	public void removeCase(String name) {
		if (cases.containsKey(name))
			cases.remove(name);
	}

	@SuppressWarnings("rawtypes")
	HashMap<String, IOutlet> outlets = new HashMap<String, IOutlet>();

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public <P extends IPlug, H, T extends IOutlet<P, H>> T getOutlet(String name) {
		T outlet = null;
		if (outlets.containsKey(name))
			outlet = (T) outlets.get(name);
		else {
			outlet = (T) new PlaceholderOutlet();
			outlets.put(name, outlet);
		}
		return outlet;
	}

	@Override
	public <P extends IPlug, H, T extends IOutlet<P, H>> void addOutlet(
			String name, T outlet) {
		Logger.debug("addOutlet : " + name);
		if (outlets.containsKey(name) && outlet != null) {
			IOutlet<?, ?> oldOutlet = outlets.get(name);
			if (oldOutlet instanceof PlaceholderOutlet)
				((PlaceholderOutlet<?, ?>) oldOutlet).transfer(outlet);
		}
		if (outlet == null)
			outlets.remove(name);
		else
			outlets.put(name, outlet);
	}

	@Override
	public void removeOutlet(String name) {
		if (outlets.containsKey(name)) {
			outlets.remove(name);
		}
	}

	Map<String, IPlug> plugs = new HashMap<String, IPlug>();

	@Override
	public void addPlug(String name, IPlug plug) {
		plugs.put(name, plug);
	}

	@Override
	public IPlug getPlug(String name) {
		return plugs.get(name);
	}

	@Override
	public void removePlug(String name) {
		plugs.remove(name);
	}

	Map<Class<?>, IService> services = new HashMap<Class<?>, IService>();

	@SuppressWarnings("unchecked")
	@Override
	public <T extends IService> T getService(Class<?> klass) {
		return (T) services.get(klass);
	}

	@Override
	public void addService(Class<?> klass, IService service) {
		services.put(klass, service);
	}

	public IImageService getImageService() {
		return getService(IImageService.class);
	}

	IDataSet ds = null;

	@Override
	public IDataSet getDataSet() {
		if (ds == null)
			ds = new DataSet();
		return ds;
	}

	@Override
	public void onCreateEnvironment() {
		Logger.debug("onCreateEnvironment");
		onRegisterShortNames();
		addService(IImageService.class, new ImageService(getContentResolver()));
		onAddServices();
		onAddCases();
	}

	@Override
	public void onDestroyEnvironment() {
		Logger.debug("onDestroyEnvironment");
		activeCase = null;
		cachedLayoutInflater.clear();
		cases.clear();
		outlets.clear();
		plugs.clear();
		services.clear();
		shortNames.clear();
		ds = null;
	}

	@Override
	public Object getSystemService(String name) {
		Object service = super.getSystemService(name);
		if (service instanceof LayoutInflater) {
			return getLayoutInflater((LayoutInflater) service);
		}
		return service;
	}

	private static final Map<String, String> shortNames = new HashMap<String, String>();

	@Override
	public void registerShortName(String prefix, String... classess) {
		for (String clazz : classess) {
			shortNames.put(clazz, prefix);
		}
	}
}
