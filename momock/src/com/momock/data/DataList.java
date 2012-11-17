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
package com.momock.data;

import java.util.ArrayList;

import com.momock.event.Event;
import com.momock.event.IEvent;
import com.momock.event.IEventHandler;

public class DataList<T> implements IDataMutableList<T>, IDataChangedAware{
	ArrayList<T> list = new ArrayList<T>();

	@Override
	public T getItem(int index) {
		return list.get(index);
	}

	@Override
	public int getItemCount() {
		return list.size();
	}

	@Override
	public void addItem(T val) {
		list.add(val);
	}

	@Override
	public void insertItem(int index, T val) {
		list.add(index, val);	
	}

	@Override
	public void setItem(int index, T val) {
		list.set(index, val);
	}

	@Override
	public void removeItem(T val) {
		list.remove(val);
	}

	@Override
	public void removeItemAt(int index) {
		list.remove(index);
	}

	// IDataChangedAware implementation
	IEvent<DataChangedEventArgs> dataChanged = null;

	@Override
	public void fireDataChangedEvent(Object sender, DataChangedEventArgs args) {
		if (sender == null) sender = this;
		if (dataChanged != null)
			dataChanged.fireEvent(sender, args);
	}

	@Override
	public void addDataChangedHandler(
			IEventHandler<DataChangedEventArgs> handler) {
		if (dataChanged == null)
			dataChanged = new Event<DataChangedEventArgs>();
		dataChanged.addEventHandler(handler);
	}

	@Override
	public void removeDataChangedHandler(
			IEventHandler<DataChangedEventArgs> handler) {
		if (dataChanged == null)
			return;
		dataChanged.removeEventHandler(handler);
	}
}
