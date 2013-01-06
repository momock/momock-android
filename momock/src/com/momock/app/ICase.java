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

import com.momock.data.IDataSet;
import com.momock.outlet.IOutlet;
import com.momock.outlet.IPlug;
import com.momock.service.IService;

public interface ICase<A> {
	IApplication getApplication();
	
	String getName();
	
	String getFullName();
	
	void onCreate();
	
	ICase<?> getParent();

	ICase<?> getCase(String name);
	
	ICase<?> findChildCase(String name);

	void addCase(ICase<?> kase);

	void removeCase(String name);

	@SuppressWarnings("rawtypes")
	IOutlet getOutlet(String name);

	@SuppressWarnings("rawtypes")
	void addOutlet(String name, IOutlet outlet);

	void removeOutlet(String name);

	void addPlug(String name, IPlug plug);
	
	IPlug getPlug(String name);
	
	void removePlug(String name);
	
	void run(Object... args);

	boolean isActive();
	
	ICase<?> getActiveCase();

	void setActiveCase(ICase<?> kase);

	void onActivate();

	void onDeactivate();

	Object getAttachedObject();

	boolean isAttached();
	
	void attach(A target);

	void detach();

	void onAttach(A target);

	void onDetach(A target);
	
	void onShow();
	
	void onHide();
	
	IDataSet getDataSet();

	<T extends IService> T getService(Class<T> klass);
	
	boolean onBack();
	
}
