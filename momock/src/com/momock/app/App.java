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

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Provider;

import android.accounts.AccountManager;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.DownloadManager;
import android.app.NotificationManager;
import android.app.SearchManager;
import android.app.UiModeManager;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.hardware.SensorManager;
import android.location.LocationManager;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.PowerManager;
import android.os.Vibrator;
import android.os.storage.StorageManager;
import android.service.wallpaper.WallpaperService;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityManager;

import com.momock.binder.ViewBinder;
import com.momock.data.DataSet;
import com.momock.data.IDataSet;
import com.momock.data.Settings;
import com.momock.holder.ImageHolder;
import com.momock.holder.TextHolder;
import com.momock.holder.ViewHolder;
import com.momock.inject.IInjector;
import com.momock.inject.InjectorWithResources;
import com.momock.outlet.IOutlet;
import com.momock.outlet.IPlug;
import com.momock.outlet.PlaceholderOutlet;
import com.momock.service.AsyncTaskService;
import com.momock.service.CrashReportService;
import com.momock.service.IAsyncTaskService;
import com.momock.service.ICrashReportService;
import com.momock.service.IImageService;
import com.momock.service.ILayoutInflaterService;
import com.momock.service.IMessageService;
import com.momock.service.IRService;
import com.momock.service.IService;
import com.momock.service.IUITaskService;
import com.momock.service.LayoutInflaterService;
import com.momock.service.MessageService;
import com.momock.service.RService;
import com.momock.service.UITaskService;
import com.momock.util.Logger;
import com.momock.util.MemoryHelper;

public abstract class App extends android.app.Application implements
		IApplication {
	
	static App app = null;
	
	IInjector injector = onCreateInjector();

	@Override
	public IInjector onCreateInjector(){
		return new InjectorWithResources();
	}
	Set<Class<?>> serviceCanNotStop = new HashSet<Class<?>>();
	
	public static App get() {
		if (app != null && !app.environmentCreated){
			app.onCreateEnvironment();
		}
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

	@SuppressLint("DefaultLocale")
	@Override
	public void onCreate() {
		LogConfig config = new LogConfig();
		config.level = Logger.LEVEL_DEBUG;
		config.enabled = true;
		config.name = getClass().getName();
		config.maxFiles = 5;
		onCreateLog(config);
		if (config.enabled)
			Logger.open(this, config.name, config.maxFiles, config.level);
		else
			Logger.setEnabled(false);
		app = this;
		injector.addProvider(IApplication.class, this);
		injector.addProvider(Context.class, this);
		injector.addProvider(Settings.class, new Provider<Settings>(){
			@Override
			public Settings get() {
				return getSettings();
			}			
		});
		injector.addProvider(Resources.class, getResources());
		injector.addProvider(ConnectivityManager.class, getSystemService(CONNECTIVITY_SERVICE));
		injector.addProvider(NotificationManager.class, getSystemService(NOTIFICATION_SERVICE));
		injector.addProvider(TelephonyManager.class, getSystemService(TELEPHONY_SERVICE));
		injector.addProvider(ActivityManager.class, getSystemService(ACTIVITY_SERVICE));
		injector.addProvider(SensorManager.class, getSystemService(SENSOR_SERVICE));
		injector.addProvider(AudioManager.class, getSystemService(AUDIO_SERVICE));
		injector.addProvider(PowerManager.class, getSystemService(POWER_SERVICE));
		injector.addProvider(WindowManager.class, getSystemService(WINDOW_SERVICE));		
		injector.addProvider(StorageManager.class, getSystemService(STORAGE_SERVICE));
		injector.addProvider(SearchManager.class, getSystemService(SEARCH_SERVICE));
		injector.addProvider(UiModeManager.class, getSystemService(UI_MODE_SERVICE));
		injector.addProvider(DownloadManager.class, getSystemService(DOWNLOAD_SERVICE));
		injector.addProvider(AccessibilityManager.class, getSystemService(ACCESSIBILITY_SERVICE));
		injector.addProvider(AccountManager.class, getSystemService(ACCOUNT_SERVICE));
		injector.addProvider(Vibrator.class, getSystemService(VIBRATOR_SERVICE));
		injector.addProvider(WallpaperService.class, getSystemService(WALLPAPER_SERVICE));
		injector.addProvider(WifiManager.class, getSystemService(WIFI_SERVICE));
		injector.addProvider(AlarmManager.class, getSystemService(ALARM_SERVICE));
		injector.addProvider(DevicePolicyManager.class, getSystemService(DEVICE_POLICY_SERVICE));
		injector.addProvider(LocationManager.class, getSystemService(LOCATION_SERVICE));
		injector.addProvider(LayoutInflater.class, new Provider<LayoutInflater>(){

			@Override
			public LayoutInflater get() {
				return (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
			}
			
		});
		
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
		registerShortName("com.momock.widget", "PlainListView", "PlainGridView");
	}

	// Helper methods

	WeakReference<Activity> lastActivity = null;
	@Override
	public void setCurrentActivity(Activity activity){
		Logger.debug("onActivityStarted : " + activity);
		lastActivity = new WeakReference<Activity>(activity);
	}
	public Activity getCurrentActivity() {		
		return lastActivity == null ? null : lastActivity.get();
	}

	public Context getCurrentContext(){
		Activity activity = getCurrentActivity();
		return activity == null ? this : activity;
	}
	public void runCase(String name) {
		getCase(name).run();
	}

	public void startActivity(Class<?> activityClass) {
		getCurrentContext().startActivity(new Intent(getCurrentContext(), activityClass));
	}
	
	// Implementation for IApplication interface
	protected boolean forceExit = false;
	protected ICase<?> activeCase = null;
	protected int activeActivityCount = 0;
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
	public ICase<?> findChildCase(String name){
		ICase<?> kase = getCase(name);
		if (kase == null){
			for(Map.Entry<String, ICase<?>> e : cases.entrySet()){
				kase = e.getValue().findChildCase(name);
				if (kase != null) return kase;
			}
		}
		return kase;
	}
	@Override
	public void addCase(ICase<?> kase) {
		if (!cases.containsKey(kase.getName())) {
			cases.put(kase.getName(), kase);
			inject(kase);
			kase.onCreate();
		}
	}

	@Override
	public void removeCase(String name) {
		if (cases.containsKey(name))
			cases.remove(name);
	}

	HashMap<String, IOutlet> outlets = new HashMap<String, IOutlet>();

	@Override
	public IOutlet getOutlet(String name) {
		IOutlet outlet = null;
		if (outlets.containsKey(name))
			outlet = outlets.get(name);
		else {
			outlet = new PlaceholderOutlet();
			outlets.put(name, outlet);
		}
		return outlet;
	}

	@Override
	public void addOutlet(
			String name, IOutlet outlet) {
		Logger.debug("addOutlet : " + name);
		if (outlets.containsKey(name) && outlet != null) {
			IOutlet oldOutlet = outlets.get(name);
			if (oldOutlet instanceof PlaceholderOutlet)
				((PlaceholderOutlet) oldOutlet).transfer(outlet);
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
	public <T extends IService> T getService(Class<T> klass) {
		return (T) services.get(klass);
	}

	@Override
	public void addService(Class<?> klass, IService service) {
		// if the service cannot be stopped in previous destroy, we should not add new service with the same interface
		if (serviceCanNotStop.contains(klass)){ 
			Logger.debug("Service " + service.getClass() + " is already running.");
		} else {
			services.put(klass, service);
			injector.addProvider(klass, service);			
		}
	}

	IDataSet ds = null;

	@Override
	public IDataSet getDataSet() {
		if (ds == null)
			ds = new DataSet();
		return ds;
	}
	boolean environmentCreated = false;
	boolean servicesCreated = false;
	protected void onPreCreateEnvironment() {
		injector.addProvider(Resources.class, new Provider<Resources>(){
			@Override
			public Resources get() {
				return getResources();
			}			
		});
	}
	protected void onPostCreateEnvironment() {
		
	}
	protected void onPreCreateServices() {
		
	}
	protected void onPostCreateServices() {
	}
	protected void onStaticCreate(){
		TextHolder.onStaticCreate(this);
		ImageHolder.onStaticCreate(this);
		ViewHolder.onStaticCreate(this);
		ViewBinder.onStaticCreate(this);
	}
	protected void onStaticDestroy(){
		TextHolder.onStaticDestroy(this);
		ImageHolder.onStaticDestroy(this);
		ViewHolder.onStaticDestroy(this);
		ViewBinder.onStaticDestroy(this);
	}
	protected void createServices(){
		if (servicesCreated){
			Logger.warn("createServices should not be called twice for the same session.");
			return;
		} 
		servicesCreated = true;
		destroyServices(); // try to stop the services previously keep running
		onPreCreateServices();
		
		addService(ICrashReportService.class, new CrashReportService());
		addService(ILayoutInflaterService.class, new LayoutInflaterService());
		addService(IAsyncTaskService.class, new AsyncTaskService());
		addService(IUITaskService.class, new UITaskService());
		addService(IMessageService.class, new MessageService());
		addService(IRService.class, new RService());
		onRegisterShortNames();
		
		onAddServices();

		List<Class<?>> startedServices = new ArrayList<Class<?>>();
		List<IService> waitingServices = new ArrayList<IService>();
		for(Map.Entry<Class<?>, IService> e : services.entrySet()){
			IService service = e.getValue();
			Class<?> classes[] = service.getDependencyServices();
			if (classes == null || classes.length == 0){
				Logger.debug("Start service " + service.getClass());
				inject(service);
				service.start();
				startedServices.add(service.getClass());
			} else {
				waitingServices.add(service);
			}
		}
		while(waitingServices.size() > 0){
			int started = 0;
			for(int i = 0; i < waitingServices.size(); i ++){
				IService service = waitingServices.get(i);
				Class<?> classes[] = service.getDependencyServices();
				int fits = 0;
				for (int j = 0; j < classes.length; j++){
					Class<?> cls = classes[j];
					for (int s = 0; s < startedServices.size(); s++){
						Class<?> scls = startedServices.get(s);
						if (cls.isAssignableFrom(scls)){
							fits ++;
							break;
						}
					}
				}
				if (fits == classes.length){
					Logger.debug("Start service " + service.getClass());
					inject(service);
					service.start();
					startedServices.add(service.getClass());
					waitingServices.remove(i--);
					started ++;
				}
			}
			Logger.check(started != 0, "Some dependency services are missing!");
		}
		onPostCreateServices();
	}
	void addDependentServices(Set<Class<?>> depServices, IService service){
		if (service == null) return;
		Class<?>[] klasses = service.getDependencyServices();
		if (klasses == null) return;
		for(Class<?> cls : klasses){
			IService ds = services.get(cls);
			Logger.debug("Dependent service " + ds.getClass() + " cannot stop.");
			depServices.add(cls);
			addDependentServices(depServices, ds);
		}
	}
	protected void destroyServices(){
		serviceCanNotStop.clear();
		for(Map.Entry<Class<?>, IService> e : services.entrySet()){
			if (!e.getValue().canStop()){
				serviceCanNotStop.add(e.getKey());
				Logger.debug("Service " + e.getValue().getClass() + " cannot stop.");
				addDependentServices(serviceCanNotStop, e.getValue());
			}
		}
		List<Class<?>> keys = new ArrayList<Class<?>>();
		keys.addAll(services.keySet());
		for(Class<?> cls : keys){
			if (!serviceCanNotStop.contains(cls)){
				services.get(cls).stop();		
				services.remove(cls);		
				injector.removeProvider(cls);
			}
		}		
		servicesCreated = false;
	}
	@Override
	public void onCreateEnvironment() {
		Logger.debug("onCreateEnvironment (v " + getVersion() + ")");
		if (environmentCreated) return;
		environmentCreated = true;
		forceExit = false;
		onPreCreateEnvironment();
		createServices();
		onStaticCreate();
		onAddCases();
		onPostCreateEnvironment();
	}

	@Override
	public void onDestroyEnvironment() {
		Logger.debug("onDestroyEnvironment");
		if (!environmentCreated) return;
		environmentCreated = false;		
		activeCase = null;
		cases.clear();
		outlets.clear();
		plugs.clear();
		ds = null;
		destroyServices();
		onStaticDestroy();
		if (forceExit){
			Logger.debug("Force Exit!");
			System.exit(0);
		}
	}

	@Override
	public Object getSystemService(String name) {
		Object service = super.getSystemService(name);
		if (service instanceof LayoutInflater) {
			ILayoutInflaterService layoutInflaterService = getService(ILayoutInflaterService.class);
			if (layoutInflaterService != null)
				return layoutInflaterService.getLayoutInflater((LayoutInflater) service);
		}
		return service;
	}

	@Override
	public void registerShortName(String prefix, String... classess) {
		ILayoutInflaterService layoutInflaterService = getService(ILayoutInflaterService.class);
		if (layoutInflaterService != null)
			layoutInflaterService.registerShortName(prefix, classess);
	}	

	@Override
	public void onCreateActivity() {
		if (!environmentCreated && activeActivityCount == 0)
			onCreateEnvironment();
		activeActivityCount ++;
	}

	@Override
	public void onDestroyActivity() {
		activeActivityCount --;
		if (environmentCreated && activeActivityCount == 0)
			onDestroyEnvironment();
	}

	@Override
	public String getVersion() {
		PackageInfo pInfo;
		try {
			pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
			return pInfo.versionName;
		} catch (NameNotFoundException e) {
			Logger.error(e);
		}
		return "?";
	}

	@Override
	public void onLowMemory() {
		Logger.debug("onLowMemory");
		super.onLowMemory();
		IImageService imageService = getService(IImageService.class);
		if (imageService != null)
			imageService.clearCache();
	}
	@Override
	public void inject(Object obj){
		injector.inject(obj);
	}

	@Override
	public <T> T getObjectToInject(Class<T> klass) {
		return injector.getObject(klass);
	}
	@Override
	public void checkMemory(){
		if (MemoryHelper.getAvailableMemory() < 2 * 1024 * 1024){
			onLowMemory();
		}
	}

	public static final String DEFAULT_SETTINGS = "default-settings";
	Settings settings = null;
	@Override
	public Settings getSettings() {
		if (settings == null)
			settings = new Settings(this, DEFAULT_SETTINGS);
		return settings;
	}
	@Override
	public void exit(){
		Logger.warn("Try to force exit!");
		forceExit = true;
		if (getCurrentActivity() != null)
			getCurrentActivity().finish();
	}
	@Override
	public boolean isExiting(){
		return forceExit;
	}

	public static boolean isEnvironmentCreated() {
		if (app == null)
			return false;
		
		if (!app.environmentCreated)
			return false;
		
		return true;
	}
}
