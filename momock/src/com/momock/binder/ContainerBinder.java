/*******************************************************************************
 * Copyright 2013 momock.com
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

import java.lang.ref.WeakReference;

import android.view.View;
import android.view.ViewGroup;

import com.momock.data.IDataList;
import com.momock.event.Event;
import com.momock.event.EventArgs;
import com.momock.event.IEvent;
import com.momock.event.ItemEventArgs;
import com.momock.holder.ViewHolder;
import com.momock.util.Convert;
import com.momock.util.Logger;

public abstract class ContainerBinder<T extends ViewGroup> implements IContainerBinder {
	protected IItemBinder itemBinder;
	protected IDataList<?> dataSource;
	protected WeakReference<ViewGroup> refContainerView = null;
	protected IEvent<ItemEventArgs> itemClickedEvent = new Event<ItemEventArgs>();
	protected IEvent<ItemEventArgs> itemSelectedEvent = new Event<ItemEventArgs>();
	protected IEvent<EventArgs> dataChangedEvent = new Event<EventArgs>();

	public ContainerBinder(IItemBinder itemBinder){
		this.itemBinder = itemBinder;
	}
	@Override
	public IDataList<?> getDataSource() {
		return dataSource;
	}
	@Override
	public IEvent<EventArgs> getDataChangedEvent() {
		return dataChangedEvent;
	}

	public IEvent<ItemEventArgs> getItemClickedEvent() {
		return itemClickedEvent;
	}

	public IEvent<ItemEventArgs> getItemSelectedEvent() {
		return itemSelectedEvent;
	}

	@Override
	public View getViewOf(int index) {
		ViewGroup parent = getContainerView();
		if (parent != null){
			for(int i = 0; i < parent.getChildCount(); i++){
				View c = parent.getChildAt(i);
				if (Convert.toInteger(c.getTag()) == index) return c;
			}
		}
		return null;
	}

	protected abstract void onBind(T containerView, IDataList<?> dataSource);
	public void bind(ViewHolder containerViewHolder, IDataList<?> dataSource){
		Logger.check(containerViewHolder != null && containerViewHolder.getView() instanceof ViewGroup, "containerViewHolder must be contains a ViewGroup!");		
		bind((ViewGroup)containerViewHolder.getView(), dataSource);
	}
	@SuppressWarnings("unchecked")
	@Override
	public void bind(ViewGroup containerView, IDataList<?> dataSource) {
		Logger.check(containerView != null && dataSource != null, "containerView and dataSource must not be null!");
		this.dataSource = dataSource;
		this.refContainerView = new WeakReference<ViewGroup>(containerView);
		onBind((T)containerView, dataSource);
	}

	@Override
	public ViewGroup getContainerView() {
		return refContainerView == null ? null : refContainerView.get();
	}

	@Override
	public IItemBinder getItemBinder() {
		return itemBinder;
	}

}
