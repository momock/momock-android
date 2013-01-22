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
import android.content.Context;

import com.momock.data.IDataSet;
import com.momock.outlet.IOutlet;
import com.momock.outlet.IPlug;
import com.momock.service.IService;

public interface IApplication {
	public static class LogConfig{
		public String name;
		public int level;
		public int maxFiles;
		public boolean enabled;
	};
	
	ICase<?> getActiveCase();

	void setActiveCase(ICase<?> kase);	

	ICase<?> getCase(String name);

	ICase<?> findChildCase(String name);

	void addCase(ICase<?> kase);

	void removeCase(String name);

	IOutlet getOutlet(String name);

	void addOutlet(String name, IOutlet outlet);

	void removeOutlet(String name);
	
	void addPlug(String name, IPlug plug);
	
	IPlug getPlug(String name);
	
	void removePlug(String name);
	
	<T extends IService> T getService(Class<T> klass);
	
	void addService(Class<?> klass, IService service);
	
	void registerShortName(String prefix, String... classess);
	
	IDataSet getDataSet();
	
	void onCreateActivity();
	
	void onDestroyActivity();	
	
	void onCreateEnvironment();
	
	void onDestroyEnvironment();	
	
	String getVersion();	
	
	void inject(Object obj);
	
	<T> T getObjectToInject(Class<T> klass);
	
	void onCreateLog(LogConfig config);
	
	Context getCurrentContext();
	
	Activity getCurrentActivity();
	
	void setCurrentActivity(Activity activity);
}
