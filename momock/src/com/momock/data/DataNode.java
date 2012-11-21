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

import java.util.List;

import com.momock.event.Event;
import com.momock.event.IEvent;
import com.momock.event.IEventHandler;
import com.momock.util.Logger;

public class DataNode implements IDataNode {
	private class MapAndList
	{
		public DataMap<String, Object> map = null;
		public DataList<Object> list = null;
	}
	protected String name = null;
	protected Object value = null;
	protected IDataNode parent = null;
	protected MapAndList getMapAndList()
	{
		if (value == null)
			value = new MapAndList();
		return value instanceof MapAndList ? (MapAndList)value : null;
	}
	protected DataMap<String, Object> getMap()
	{
		MapAndList ml = getMapAndList();
		if (ml == null) 
		{
			Logger.warn("Try to access property in a value node!");
			return null;
		}
		if (ml.map == null)
			ml.map = new DataMap<String, Object>();
		return ml.map;
	}
	protected DataList<Object> getList()
	{
		MapAndList ml = getMapAndList();
		if (ml == null)
		{
			Logger.warn("Try to access child item in a value node!");
			return null;
		}
		if (ml.list == null)
			ml.list = new DataList<Object>();
		return ml.list;		
	}
	@Override
	public boolean isValueNode()
	{
		return !(value instanceof MapAndList);
	}
	@Override
	public Object getValue() {
		if (!isValueNode()) return null;
		return value;
	}
	@Override
	public void setValue(Object value) {
		this.value = value;
	}
	@Override
	public IDataNode getParent() {
		return parent;
	}
	@Override
	public String getName() {
		return name;
	}	
	// IDataMutableMap implementation
	@Override
	public void setProperty(String name, Object val) {
		DataMap<String, Object> map = getMap();
		if (map == null) return;
		map.setProperty(name, val);		
	}
	@Override
	public boolean hasProperty(String name) {
		DataMap<String, Object> map = getMap();
		if (map == null) return false;
		return map.hasProperty(name);
	}
	@Override
	public Object getProperty(String name) {
		DataMap<String, Object> map = getMap();
		if (map == null) return false;
		return map.getProperty(name);
	}
	@Override
	public List<String> getPropertyNames() {
		DataMap<String, Object> map = getMap();
		if (map == null) return null;
		return map.getPropertyNames();
	}
	// IDataMutableList implementation
	@Override
	public void addItem(Object val) {
		DataList<Object> list = getList();
		if (list == null) return;
		list.addItem(val);
	}
	@Override
	public void insertItem(int index, Object val) {
		DataList<Object> list = getList();
		if (list == null) return;
		list.insertItem(index, val);
	}
	@Override
	public void setItem(int index, Object val) {
		DataList<Object> list = getList();
		if (list == null) return;
		list.setItem(index, val);
	}
	@Override
	public void removeItem(Object val) {
		DataList<Object> list = getList();
		if (list == null) return;
		list.removeItem(val);
	}
	@Override
	public void removeItemAt(int index) {
		DataList<Object> list = getList();
		if (list == null) return;
		list.removeItemAt(index);
	}
	@Override
	public Object getItem(int index) {
		DataList<Object> list = getList();
		if (list == null) return null;
		return list.getItem(index);
	}
	@Override
	public int getItemCount() {
		DataList<Object> list = getList();
		if (list == null) return 0;
		return list.getItemCount();
	}
	// IDataChangedAware implementation
	IEvent<DataChangedEventArgs> dataChanged = null;
	@Override
	public void fireDataChangedEvent(Object sender, DataChangedEventArgs args) {
		if (sender == null) sender = this;
		if (dataChanged != null)
			dataChanged.fireEvent(sender, args);
		if (parent != null)
			parent.fireDataChangedEvent(sender, args);
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
		if (dataChanged == null) return;
		dataChanged.removeEventHandler(handler);
	}
	@Override
	public boolean hasItem(Object item) {
		DataList<Object> list = getList();
		if (list == null) return false;
		return list.hasItem(item);
	}
}
