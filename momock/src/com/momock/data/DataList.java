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
import java.util.List;

import com.momock.event.Event;
import com.momock.event.IEvent;
import com.momock.event.IEventHandler;

public class DataList<T> implements IDataMutableList<T>{
	List<T> list = null;

	public DataList(){
		list = new ArrayList<T>();
	}
	
	public DataList(List<T> list){
		this.list = list;
	}
	
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
		fireDataChangedEvent();
	}

	@Override
	public void insertItem(int index, T val) {
		list.add(index, val);	
		fireDataChangedEvent();
	}

	@Override
	public void setItem(int index, T val) {
		list.set(index, val);
		fireDataChangedEvent();
	}

	@Override
	public void removeItem(T val) {
		list.remove(val);
		fireDataChangedEvent();
	}

	@Override
	public void removeItemAt(int index) {
		list.remove(index);
		fireDataChangedEvent();
	}

	@Override
	public boolean hasItem(T item) {
		return list.contains(item);
	}

	@Override
	public void removeAllItems() {
		list.clear();
		fireDataChangedEvent();
	}
	
	// IDataChangedAware implementation
	IEvent<DataChangedEventArgs> dataChanged = null;
	int batchLevel = 0;
	boolean isDataDirty = false;

	@Override
	public void fireDataChangedEvent() {
		if (batchLevel > 0){
			isDataDirty = true;
		} else {
			if (dataChanged != null)
				dataChanged.fireEvent(this, new DataChangedEventArgs());
		}
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

	@Override
	public void beginBatchChange() {
		if (batchLevel == 0)
			isDataDirty = false;		
		batchLevel ++;
	}

	@Override
	public void endBatchChange() {
		batchLevel --;
		if (batchLevel == 0 && isDataDirty)
			fireDataChangedEvent();
	}
}
