/*******************************************************************************
 * Copyright 2015 momock.com
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

package com.momock.binder;

import java.util.ArrayList;

import com.momock.util.Logger;

import android.view.View;

public class ComposedItemBinder implements IComposedItemBinder {
	
	ArrayList<IBinderSelector> selectors = new ArrayList<IBinderSelector>();
	ArrayList<IItemBinder> binders = new ArrayList<IItemBinder>();
	
	public void addBinder(IBinderSelector selector, IItemBinder binder){
		selectors.add(selector);
		binders.add(binder);
	}
	@Override
	public View onCreateItemView(View convertView, int position, IContainerBinder container) {
		Object item = container.getDataSource().getItem(position);
		int index = getBinderIndex(item);
		if (index == -1)
			return null;
		else {
			IItemBinder binder = binders.get(index);
			return binder.onCreateItemView(convertView, position, container);
		}
	}
	@Override
	public int getBinderIndex(Object item) {
		for (int i = 0; i < selectors.size(); i++){
			IBinderSelector selector = selectors.get(i);
			if (selector.onSelect(item)) return i;
		}
		Logger.check(false, "Fails to find binder for item " + item);
		return -1;
	}
	@Override
	public int getBinderCount() {
		return selectors.size();
	}

}
