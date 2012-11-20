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
package com.momock.outlet.action;

import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.momock.binder.ViewBinder;
import com.momock.holder.ViewHolder;
import com.momock.outlet.Outlet;

public class ListViewActionOutlet extends Outlet<IActionPlug>{
	public static interface OnCreateItemViewHandler {
		View onBindItemView(View convertView, IActionPlug plug, ViewGroup parent);
	}
	OnCreateItemViewHandler handler = null;
	public ListViewActionOutlet(OnCreateItemViewHandler handler){
		this.handler = handler;
	}
	@Override
	public void onAttach(Object target) {
		final ListView lv = (ListView)target;
		if (lv != null)
		{
			lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {				
				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					IActionPlug plug = (IActionPlug)plugs.get(position);
					plug.getExecuteEvent().fireEvent(plug, null);					
				}
			});
			lv.setAdapter(new BaseAdapter(){

				@Override
				public int getCount() {
					return plugs.size();
				}

				@Override
				public Object getItem(int position) {
					return plugs.get(position);
				}

				@Override
				public long getItemId(int position) {
					return position;
				}

				@Override
				public View getView(int position, View convertView,
						ViewGroup parent) {
					return handler.onBindItemView(convertView, plugs.get(position), parent);
				}
				
			});
		}
	}
	
	public static ListViewActionOutlet getSimple(final String propName){
		final ViewBinder binder = new ViewBinder();
		binder.link(propName, android.R.id.text1);
		return new ListViewActionOutlet(new ListViewActionOutlet.OnCreateItemViewHandler() {
			
			@Override
			public View onBindItemView(View convertView, IActionPlug plug,
					ViewGroup parent) {
				View view = convertView;
				if (view == null){
					view = ViewHolder.get(android.R.layout.simple_list_item_1).getView();
				}
				binder.bind(view, plug);
				return view;
			}
		});		
	}

}
