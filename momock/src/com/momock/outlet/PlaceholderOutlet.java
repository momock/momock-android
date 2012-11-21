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

import junit.framework.Assert;

import com.momock.data.DataList;
import com.momock.data.IDataList;
import com.momock.data.IDataMutableList;

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
	public IDataList<P> getAllPlugs() {
		return plugs;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void transfer(IOutlet outlet) {
		for (int i = 0; i < plugs.getItemCount(); i++)
			outlet.addPlug(plugs.getItem(i));
	}

	@Override
	public T getAttachedObject() {
		Assert.assertTrue(false);
		return null;
	}

	@Override
	public void attach(T target) {
		Assert.assertTrue(false);
	}

	@Override
	public void detach() {
		Assert.assertTrue(false);
	}

	@Override
	public void onAttach(T target) {
		Assert.assertTrue(false);
	}

	@Override
	public void onDetach(T target) {
		Assert.assertTrue(false);
	}

	@Override
	public P getActivePlug() {
		Assert.assertTrue(false);
		return null;
	}

	@Override
	public void setActivePlug(P plug) {		
		Assert.assertTrue(false);
	}

	@Override
	public void onActivate(P plug) {
		Assert.assertTrue(false);		
	}

	@Override
	public void onDeactivate(P plug) {
		Assert.assertTrue(false);		
	}

}
