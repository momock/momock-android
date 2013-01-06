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
import android.widget.BaseAdapter;

import com.momock.binder.ContainerBinder;
import com.momock.binder.IItemBinder;
import com.momock.data.IDataList;
import com.momock.event.EventArgs;
import com.momock.event.ItemEventArgs;
import com.momock.util.Logger;
import com.momock.widget.IPlainAdapterView;

public class PlainAdapterViewBinder<T extends IPlainAdapterView> extends ContainerBinder<ViewGroup> {

	public PlainAdapterViewBinder(IItemBinder binder) {
		super(binder);
	}

	BaseAdapter adapter = null;
	public BaseAdapter getAdapter(){
		return adapter;
	}
	
	public void onBind(final ViewGroup v, final IDataList<?> list) {		
		if (v != null) {
			final IPlainAdapterView view = (IPlainAdapterView)v;
			view.setOnItemClickListener(new IPlainAdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(IPlainAdapterView parent, View v,
						int index) {
					ItemEventArgs args = new ItemEventArgs((View) view, index,
							list.getItem(index));
					itemClickedEvent.fireEvent(v, args);
				}
			});
			adapter = new BaseAdapter() {

				@Override
				public int getCount() {
					return list.getItemCount();
				}

				@Override
				public Object getItem(int position) {
					return list.getItem(position);
				}

				@Override
				public long getItemId(int position) {
					return position;
				}

				@Override
				public View getView(int position, View convertView,	ViewGroup parent) {
					Object item = getItem(position);
					View view = itemBinder.onCreateItemView(convertView, item, PlainAdapterViewBinder.this);
					view.setTag(item);
					return view;
				}

				@Override
				public void notifyDataSetChanged() {
					getDataChangedEvent().fireEvent(this, new EventArgs());
					super.notifyDataSetChanged();
					Logger.debug("PlainAdapterViewBinder.BaseAdapter.notifyDataSetChanged");
				}

				@Override
				public void notifyDataSetInvalidated() {
					super.notifyDataSetInvalidated();
					Logger.debug("PlainAdapterViewBinder.BaseAdapter.notifyDataSetChanged");
				}

			};			
			view.setAdapter(adapter);
		}
	}
}
