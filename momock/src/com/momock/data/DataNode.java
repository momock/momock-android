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
import java.util.Map;

public class DataNode implements IDataNode {
	private class MapAndList
	{
		public Map<String, IDataNode> map = new HashMap<String, IDataNode>();
		public List<IDataNode> list = new ArrayList<IDataNode>();
	}
	protected String name;
	protected Object value;
	protected IDataNode parent;
	public boolean isValue()
	{
		return !(value instanceof MapAndList);
	}
	public boolean isPropertyNode()
	{
		if (name == null || parent == null || parent.isValue()) return false;
		MapAndList mapAndList = (MapAndList)parent.getValue();
		return mapAndList.map.containsKey(name);
	}
	protected void ensureMapAndList()
	{
		if (value instanceof MapAndList) return;
		value = new MapAndList();
	}
	public Object getValue() {
		if (!isValue()) return null;
		return value;
	}
	public void setValue(Object value) {
		this.value = value;
	}
	public IDataNode getParent() {
		return parent;
	}
	public String getName() {
		return name;
	}
}
