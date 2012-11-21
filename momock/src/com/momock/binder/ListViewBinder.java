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

import junit.framework.Assert;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.momock.data.IDataList;
import com.momock.event.Event;
import com.momock.event.EventArgs;
import com.momock.event.IEvent;
import com.momock.event.IEventHandler;
import com.momock.holder.ViewHolder;

public class ListViewBinder {
	public static class ItemClickedEventArgs extends EventArgs {
		int index;
		Object item;
		ListView listView;

		public ItemClickedEventArgs(ListView listView, int index, Object item) {
			this.listView = listView;
			this.index = index;
			this.item = item;
		}

		public int getIndex() {
			return index;
		}

		public Object getItem() {
			return item;
		}

		public ListView getListView() {
			return listView;
		}
	}

	IEvent<ItemClickedEventArgs> itemClickedEvent = new Event<ItemClickedEventArgs>();

	public IEvent<ItemClickedEventArgs> getItemClickedEvent() {
		return itemClickedEvent;
	}

	public ListViewBinder addItemClickedEventHandler(IEventHandler<ItemClickedEventArgs> handler){
		itemClickedEvent.addEventHandler(handler);
		return this;
	}
	public static abstract class ItemViewBinder extends ViewBinder {
		protected abstract View onCreateItemView(View convertView, int index, Object item,
				ViewGroup parent);
	}
	ItemViewBinder binder;
	public ListViewBinder(ItemViewBinder binder){
		this.binder = binder;
	}
	public void bind(ViewHolder view, IDataList<?> list) {
		Assert.assertTrue(view.getView() instanceof ListView);
		bind((ListView) view.getView(), list);
	}

	public void bind(ListView lv, final IDataList<?> list) {
		if (lv != null) {
			lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					ItemClickedEventArgs args = new ItemClickedEventArgs(
							(ListView) parent, position, list.getItem(position));
					itemClickedEvent.fireEvent(view, args);
				}
			});
			lv.setAdapter(new BaseAdapter() {

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
				public View getView(int position, View convertView,
						ViewGroup parent) {
					return binder.onCreateItemView(convertView, position,
							getItem(position), parent);
				}

			});
		}
	}
	public static ItemViewBinder getSimpleItemViewBinder(final String propName){
		return new ListViewBinder.ItemViewBinder(){
			@Override
			protected View onCreateItemView(View convertView, int index,
					Object plug, ViewGroup parent) {
				View view = convertView;
				if (view == null) {
					view = ViewHolder.get(android.R.layout.simple_list_item_1)
							.getView();
				}
				bind(view, plug);
				return view;
			}

			@Override
			protected void onCreate() {
				link(propName, android.R.id.text1);
			}
		};
	}
	public static ListViewBinder getSimple(String propName){
		return new ListViewBinder(ListViewBinder.getSimpleItemViewBinder(propName));
	}

}
