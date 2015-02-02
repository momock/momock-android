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

import com.momock.data.DataList;
import com.momock.data.IDataList;
import com.momock.data.IDataMutableList;
import com.momock.event.Event;
import com.momock.event.EventArgs;
import com.momock.event.IEvent;
import com.momock.event.ItemEventArgs;

public class Outlet implements IOutlet {
	private IDataMutableList<IPlug> plugs = new DataList<IPlug>();
	protected IEvent<EventArgs> activePlugChangedEvent = new Event<EventArgs>();
	protected IPlug activePlug = null;

	@Override
	public IPlug addPlug(IPlug plug) {
		if (!plugs.hasItem(plug))
			plugs.addItem(plug);
		return plug;
	}

	@Override
	public void removePlug(IPlug plug) {
		plugs.removeItem(plug);
	}

	@Override
	public IDataList<IPlug> getPlugs() {
		return provider == null ? plugs : provider.getPlugs();
	}

	@Override
	public IPlug getActivePlug() {
		return activePlug;
	}

	@Override
	public void setActivePlug(IPlug plug) {
		if (activePlug != plug) {
			if (activePlug != null)
				onDeactivate(activePlug);
			activePlug = plug;
			if (activePlug != null){
				onActivate(activePlug);
				activePlugChangedEvent.fireEvent(this, null);
			}
		}
	}
	@Override
	public IEvent<EventArgs> getActivePlugChangedEvent() {
		return activePlugChangedEvent;
	}
	@Override
	public void onActivate(IPlug plug) {
		if (plug != null) plug.onActivate();		
	}

	@Override
	public void onDeactivate(IPlug plug) {
		if (plug != null) plug.onDeactivate();		
	}

	IPlugProvider provider = null;
	@Override
	public IPlugProvider getPlugProvider() {
		return provider == null ? this : provider;
	}

	@Override
	public void setPlugProvider(IPlugProvider provider) {
		this.provider = provider;		
	}

	@Override
	public int getIndexOf(IPlug plug) {
		IDataList<IPlug> plugs = getPlugs();
		for(int i = 0; i < plugs.getItemCount(); i++){
			if (plugs.getItem(i) == plug) return i;		
		}		
		return -1;
	}

}
