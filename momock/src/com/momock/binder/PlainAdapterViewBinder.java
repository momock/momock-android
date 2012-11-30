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
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.momock.app.App;
import com.momock.binder.ViewBinder.Setter;
import com.momock.data.IDataList;
import com.momock.event.Event;
import com.momock.event.IEvent;
import com.momock.event.IEventHandler;
import com.momock.event.ItemEventArgs;
import com.momock.holder.ImageHolder;
import com.momock.holder.ViewHolder;
import com.momock.widget.IPlainAdapterView;

public class PlainAdapterViewBinder<T extends IPlainAdapterView> {

	IEvent<ItemEventArgs> itemClickedEvent = new Event<ItemEventArgs>();

	public IEvent<ItemEventArgs> getItemClickedEvent() {
		return itemClickedEvent;
	}

	public PlainAdapterViewBinder<T> addItemClickedEventHandler(
			IEventHandler<ItemEventArgs> handler) {
		itemClickedEvent.addEventHandler(handler);
		return this;
	}

	protected ItemViewBinder binder;

	public PlainAdapterViewBinder(ItemViewBinder binder) {
		this.binder = binder;
	}

	Setter imageSetter = null;

	@SuppressWarnings("unchecked")
	public void bind(ViewHolder view, IDataList<?> list) {
		bind((T) view.getView(), list);
	}

	public void bind(final T view, final IDataList<?> list) {
		if (view != null) {
			view.setOnItemClickListener(new IPlainAdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(IPlainAdapterView parent, View v,
						int index) {
					ItemEventArgs args = new ItemEventArgs((View) view, index,
							list.getItem(index));
					itemClickedEvent.fireEvent(v, args);
				}
			});
			final BaseAdapter adapter = new BaseAdapter() {

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

			};
			if (imageSetter != null)
				binder.removeSetter(imageSetter);
			imageSetter = new Setter() {

				@Override
				public boolean onSet(View view, String viewProp, Object val) {
					if (view instanceof ImageView) {
						if (viewProp == null) {
							if (val instanceof CharSequence) {
								ImageHolder ih = ImageHolder.create(val
										.toString());
								if (ih != null && ih.getAsBitmap() != null) {
									((ImageView) view).setImageBitmap(ih
											.getAsBitmap());
									return true;
								} else {
									App.get()
											.getImageService()
											.load(adapter, (ImageView) view,
													val.toString());
									return true;
								}
							}
						}
					}
					return false;
				}

			};
			binder.addSetter(imageSetter);
			((T) view).setAdapter(adapter);
		}
	}
}
