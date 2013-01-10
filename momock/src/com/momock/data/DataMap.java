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
import java.util.HashMap;
import java.util.List;

import com.momock.event.Event;
import com.momock.event.IEvent;
import com.momock.event.IEventHandler;

public class DataMap<K, V> implements IDataMutableMap<K, V> {
	HashMap<K, V> map = new HashMap<K, V>();

	@Override
	public V getProperty(K name) {
		return map.get(name);
	}

	@Override
	public List<K> getPropertyNames() {
		List<K> names = new ArrayList<K>();
		names.addAll(map.keySet());
		return names;
	}

	@Override
	public void setProperty(K name, V val) {
		map.put(name, val);
		fireDataChangedEvent();
	}

	@Override
	public boolean hasProperty(K name) {
		return map.containsKey(name);
	}

	@Override
	public void copyPropertiesFrom(IDataMap<K, V> srouce) {
		beginBatchChange();
		for(K name : srouce.getPropertyNames()){
			setProperty(name, srouce.getProperty(name));
		}
		endBatchChange();
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
