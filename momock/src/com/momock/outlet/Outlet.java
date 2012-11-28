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

import com.momock.data.DataChangedEventArgs;
import com.momock.data.DataList;
import com.momock.data.IDataChangedAware;
import com.momock.data.IDataList;
import com.momock.data.IDataMutableList;
import com.momock.event.Event;
import com.momock.event.IEvent;
import com.momock.event.IEventHandler;

public class Outlet<P extends IPlug, T> implements IOutlet<P, T>,
		IDataChangedAware {
	private IDataMutableList<P> plugs = new DataList<P>();
	protected P activePlug = null;

	@Override
	public P addPlug(P plug) {
		if (!plugs.hasItem(plug))
			plugs.addItem(plug);
		return plug;
	}

	@Override
	public void removePlug(P plug) {
		plugs.removeItem(plug);
	}

	@Override
	public IDataList<P> getPlugs() {
		return provider == null ? plugs : provider.getPlugs();
	}

	// IDataChangedAware implementation
	IEvent<DataChangedEventArgs> dataChanged = null;

	@Override
	public void fireDataChangedEvent(Object sender, DataChangedEventArgs args) {
		if (sender == null)
			sender = this;
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
	T attachedObject = null;

	@Override
	public T getAttachedObject() {
		return attachedObject;
	}

	@Override
	public void attach(T target) {
		detach();
		if (target != null) {
			attachedObject = target;
			onAttach(target);
		}
	}

	@Override
	public void detach() {
		if (getAttachedObject() != null) {
			onDetach(attachedObject);
			attachedObject = null;
		}
	}

	@Override
	public void onAttach(T target) {

	}

	@Override
	public void onDetach(T target) {

	}

	@Override
	public P getActivePlug() {
		return activePlug;
	}

	@Override
	public void setActivePlug(P plug) {
		if (activePlug != plug) {
			if (activePlug != null)
				onDeactivate(activePlug);
			activePlug = plug;
			if (activePlug != null)
				onActivate(activePlug);
		}
	}

	@Override
	public void onActivate(P plug) {
		if (plug != null) plug.onActivate();		
	}

	@Override
	public void onDeactivate(P plug) {
		if (plug != null) plug.onDeactivate();		
	}

	IPlugProvider<P> provider = null;
	@Override
	public IPlugProvider<P> getPlugProvider() {
		return provider == null ? this : provider;
	}

	@Override
	public void setPlugProvider(IPlugProvider<P> provider) {
		this.provider = provider;		
	}

	@Override
	public int getIndexOf(P plug) {
		IDataList<P> plugs = getPlugs();
		for(int i = 0; i < plugs.getItemCount(); i++){
			if (plugs.getItem(i) == plug) return i;		
		}		
		return -1;
	}

}
