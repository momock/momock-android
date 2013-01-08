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
package com.momock.binder.container;

import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;

import com.momock.binder.ContainerBinder;
import com.momock.binder.IItemBinder;
import com.momock.data.DataChangedEventArgs;
import com.momock.data.IDataList;
import com.momock.event.EventArgs;
import com.momock.event.IEventHandler;
import com.momock.event.ItemEventArgs;
import com.momock.util.Logger;

public class AdapterViewBinder<T extends AdapterView<?>> extends ContainerBinder<T> {
	
	public AdapterViewBinder(IItemBinder binder) {
		super(binder);
	}

	BaseAdapter adapter = null;
	public BaseAdapter getAdapter(){
		return adapter;
	}
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void onBind(T view, final IDataList<?> dataSource) {
		if (view != null) {
			view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					ItemEventArgs args = new ItemEventArgs(parent, position,
							dataSource.getItem(position));
					itemClickedEvent.fireEvent(view, args);
				}
			});
			view.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

				@Override
				public void onItemSelected(AdapterView<?> parent, View view,
						int position, long id) {
					ItemEventArgs args = new ItemEventArgs(parent, position,
							dataSource.getItem(position));
					itemSelectedEvent.fireEvent(view, args);
				}

				@Override
				public void onNothingSelected(AdapterView<?> parent) {
					Logger.debug("onNothingSelected");
				}
			});
			adapter = new BaseAdapter() {

				@Override
				public int getCount() {
					return dataSource.getItemCount();
				}

				@Override
				public Object getItem(int position) {
					return dataSource.getItem(position);
				}

				@Override
				public long getItemId(int position) {
					return position;
				}

				@Override
				public View getView(int position, View convertView,
						ViewGroup parent) {
					Object item = getItem(position);
					View view = itemBinder.onCreateItemView(convertView, item, AdapterViewBinder.this);					
					return view;
				}

				@Override
				public void notifyDataSetChanged() {
					getDataChangedEvent().fireEvent(this, new EventArgs());
					super.notifyDataSetChanged();
					Logger.debug("AdapterViewBinder.BaseAdapter.notifyDataSetChanged");
				}

				@Override
				public void notifyDataSetInvalidated() {
					super.notifyDataSetInvalidated();
					Logger.debug("AdapterViewBinder.BaseAdapter.notifyDataSetInvalidated");
				}

			};
			((AdapterView) view).setAdapter(adapter);
			dataSource.addDataChangedHandler(new IEventHandler<DataChangedEventArgs>(){

				@Override
				public void process(Object sender, DataChangedEventArgs args) {
					adapter.notifyDataSetChanged();
				}
				
			});
		}
	}
}
