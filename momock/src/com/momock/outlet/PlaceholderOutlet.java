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

import java.util.ArrayList;
import java.util.List;

public class PlaceholderOutlet<T extends IPlug> implements IOutlet<T> {
	List<T> plugs = new ArrayList<T>();

	@Override
	public T addPlug(T plug) {
		plugs.add(plug);
		return plug;
	}

	@Override
	public void removePlug(IPlug plug) {
		plugs.remove(plug);
	}

	@SuppressWarnings("unchecked")
	@Override
	public T[] getAllPlugs() {
		return (T[]) plugs.toArray();
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void transfer(IOutlet outlet) {
		for (int i = 0; i < plugs.size(); i++)
			outlet.addPlug(plugs.get(i));
	}

	@Override
	public Object getAttachedObject() {
		return null;
	}

	@Override
	public void attach(Object target) {
	}

	@Override
	public void detach() {
	}

	@Override
	public void onAttach(Object target) {
	}

	@Override
	public void onDetach(Object target) {
	}

}
