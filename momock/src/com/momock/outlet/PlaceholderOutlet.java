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
import com.momock.util.Logger;

public class PlaceholderOutlet<P extends IPlug, T> implements IOutlet<P, T> {
	IDataMutableList<P> plugs = new DataList<P>();

	@Override
	public P addPlug(P plug) {
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

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void transfer(IOutlet outlet) {
		for (int i = 0; i < plugs.getItemCount(); i++)
			outlet.addPlug(plugs.getItem(i));
	}

	@Override
	public T getAttachedObject() {
		Logger.check(false, "PlaceholderOutlet.getAttachedObject should not be called!");
		return null;
	}

	@Override
	public void attach(T target) {
		Logger.check(false, "PlaceholderOutlet.attach should not be called!");
	}

	@Override
	public void detach() {
		Logger.check(false, "PlaceholderOutlet.detach should not be called!");
	}

	@Override
	public void onAttach(T target) {
		Logger.check(false, "PlaceholderOutlet.onAttach should not be called!");
	}

	@Override
	public void onDetach(T target) {
		Logger.check(false, "PlaceholderOutlet.onDetach should not be called!");
	}

	@Override
	public P getActivePlug() {
		Logger.check(false, "PlaceholderOutlet.getActivePlug should not be called!");
		return null;
	}

	@Override
	public void setActivePlug(P plug) {		
		Logger.check(false, "PlaceholderOutlet.setActivePlug should not be called!");
	}

	@Override
	public void onActivate(P plug) {
		Logger.check(false, "PlaceholderOutlet.onActivate should not be called!");
	}

	@Override
	public void onDeactivate(P plug) {
		Logger.check(false, "PlaceholderOutlet.onDeactivate should not be called!");
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
