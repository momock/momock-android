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

import com.momock.outlet.IOutlet;
import com.momock.outlet.IPlug;
import com.momock.outlet.PlaceholderOutlet;
import com.momock.util.Logger;


public abstract class Case implements ICase {
	public Case()
	{
		onCreate();
	}
	public Case(ICase parent){
		this.parent = parent;
		onCreate();
	}
	
	protected abstract void onCreate();
	
	@Override
	public void onActivate() {
		
	}
	@Override
	public void onDeactivate() {
		
	}
	@Override
	public void run() {
		
	}

	// IAttachable implementation
	Object attachedObject = null;
	@Override
	public Object getAttachedObject() {
		return attachedObject;
	}

	@Override
	public void attach(Object target) {
		detach();
		if (target != null)
		{
			attachedObject = target;	
			onAttach(target);
		}
	}
	
	@Override
	public void detach(){
		if (getAttachedObject() != null)
		{
			onDetach(attachedObject);
			attachedObject = null;
		}
	}
	
	@Override
	public void onAttach(Object target)
	{
		
	}
	@Override
	public void onDetach(Object target)
	{
		
	}

	// Implementation for ICase interface
	protected ICase activeCase = null;
	protected ICase parent;
	protected HashMap<String, ICase> cases = new HashMap<String, ICase>();

	@Override
	public ICase getParent() {
		return parent;
	}
	
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
	public ICase getCase(String name) {
		if (name == null)
			return null;
		ICase kase = cases.get(name);
		if (kase == null)
			kase = getParent() == null ? App.get().getCase(name) : getParent().getCase(name);
		return kase;
	}

	public void addCase(ICase kase){
		addCase(kase.getClass().getName(), kase);
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
	@SuppressWarnings({ "unchecked" })
	@Override
	public <T extends IPlug> IOutlet<T> getOutlet(String name) {
		IOutlet<T> outlet = null;
		if (outlets.containsKey(name))
			outlet = outlets.get(name);
		if (outlet == null)
			outlet = (IOutlet<T>) (getParent() == null ? App.get().getOutlet(name) : getParent().getOutlet(name));
		if (outlet == null)
		{
			outlet = new PlaceholderOutlet<T>();
			outlets.put(name, outlet);
		}
		return outlet;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends IPlug> void addOutlet(String name, IOutlet<T> outlet) {
		Logger.debug("addOutlet : " + name);
		if (outlets.containsKey(name) && outlet != null)
		{
			IOutlet<T> oldOutlet = outlets.get(name);
			if (oldOutlet instanceof PlaceholderOutlet)
				((PlaceholderOutlet<T>)oldOutlet).transfer(outlet);
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
