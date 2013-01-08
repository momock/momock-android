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
package com.momock.binder;

import android.view.View;

import com.momock.holder.ViewHolder;
import com.momock.util.Logger;

public class ItemBinder extends ViewBinder implements IItemBinder{
	int itemViewId;
	public ItemBinder(int itemViewId, int[] childViewIds, String[] props){
		Logger.check(childViewIds != null && props != null && childViewIds.length == props.length, "Parameter error!");
		this.itemViewId = itemViewId;
		for(int i = 0; i < props.length; i++){
			link(props[i], childViewIds[i]);
		}
	}
	@Override
	public View onCreateItemView(View convertView, Object item, IContainerBinder container) {
		View view = convertView;
		if (view == null) {
			view = ViewHolder.create(container.getContainerView(), itemViewId).getView();
		}
		view.setTag(item);
		bind(view, item, container);
		return view;
	}

}
