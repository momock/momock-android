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
package com.momock.outlet;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import com.momock.IAttachable;
import com.momock.app.App;
import com.momock.data.DataChangedEventArgs;
import com.momock.data.IDataChangedAware;
import com.momock.event.Event;
import com.momock.event.IEvent;
import com.momock.event.IEventHandler;
import com.momock.util.Logger;

public class Outlet<T extends IPlug> implements ICompositeOutlet<T>, IAttachable, IDataChangedAware {
	protected List<T> plugs = new ArrayList<T>();
	@Override
	public T addPlug(T plug) {
		if (!plugs.contains(plug))
			plugs.add(plug);
		return plug;
	}

	@Override
	public void removePlug(T plug) {
		plugs.remove(plug);
	}

	@SuppressWarnings("unchecked")
	@Override
	public T[] getAllPlugs()
	{
		List<T> all = new ArrayList<T>();
		all.addAll(plugs);
		for(IOutlet<T> outlet : this.getAllOutlets())
		{
			T[] ts = outlet.getAllPlugs();
			for(int i = 0; i < ts.length; i++)
			{
				if (!all.contains(ts[i]))
					all.add(ts[i]);
			}
		}
		return (T[])all.toArray();
	}
	
	// IDataChangedAware implementation
	IEvent<DataChangedEventArgs> dataChanged = null;

	@Override
	public void fireDataChangedEvent(Object sender, DataChangedEventArgs args) {
		if (sender == null) sender = this;
		if (dataChanged != null)
			dataChanged.fireEvent(sender, args);
	}

	@Override
	public void addDataChangedHandler(
			IEventHandler<DataChangedEventArgs> handler) {
		if (dataChanged == null)
			dataChanged = new Event<DataChangedEventArgs>();
		dataChanged.addEventHandler(handler);
	}

	@Override
	public void removeDataChangedHandler(
			IEventHandler<DataChangedEventArgs> handler) {
		if (dataChanged == null)
			return;
		dataChanged.removeEventHandler(handler);
	}
	
	// IAttachable implementation
	WeakReference<Object> attachedObject = null;
	@Override
	public Object getAttachedObject() {
		return attachedObject == null ? null : attachedObject.get();
	}

	@Override
	public void attach(Object target) {
		detach();
		if (target != null)
		{
			attachedObject = new WeakReference<Object>(target);	
			onAttach(target);
		}
	}
	
	@Override
	public void detach(){
		if (getAttachedObject() != null)
		{
			onDetach(attachedObject.get());
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

	// ICompositeOutlet
	@SuppressWarnings("rawtypes")
	List outlets = new ArrayList();
	@SuppressWarnings("unchecked")
	@Override
	public void addOutlet(String name) {
		outlets.add(name);
	}
	@SuppressWarnings("unchecked")
	@Override
	public void removeOutlet(String name) {
		outlets.add(name);		
	}
	@SuppressWarnings("unchecked")
	@Override
	public void addOutlet(IOutlet<T> outlet) {
		outlets.add(outlet);
	}

	@Override
	public void removeOutlet(IOutlet<T> outlet) {
		outlets.remove(outlet);
	}

	@SuppressWarnings("unchecked")
	@Override
	public IOutlet<T>[] getAllOutlets() {
		List<IOutlet<T>> all = new ArrayList<IOutlet<T>>();
		for(int i = 0; i < outlets.size(); i++)
		{
			IOutlet<T> outlet = null;
			if (outlets.get(i) instanceof String)
				outlet = (IOutlet<T>)App.get().getOutlet((String)outlets.get(i));
			else
				outlet = (IOutlet<T>)outlets.get(i);
			if (outlet != null && !all.contains(outlet))
				all.add(outlet);
		}
		return (IOutlet<T>[])all.toArray();
	}
}
