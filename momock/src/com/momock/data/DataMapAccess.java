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

import com.momock.util.Convert;

public class DataMapAccess {
	IDataMap<String, Object> dataMap = null;
	public DataMapAccess()
	{
		
	}
	public DataMapAccess(IDataMap<String, Object> map)
	{
		this.dataMap = map;
	}
	public int getPropertyAsInt(String name, int def)
	{
		Object obj = dataMap == null ? null : dataMap.getProperty(name);
		return obj == null ? def : Convert.toInteger(obj);
	}
	public long getPropertyAsLong(String name, long def)
	{
		Object obj = dataMap == null ? null : dataMap.getProperty(name);
		return obj == null ? def : Convert.toLong(obj);
	}
	public double getPropertyAsDouble(String name, double def)
	{
		Object obj = dataMap == null ? null : dataMap.getProperty(name);
		return obj == null ? def : Convert.toDouble(obj);
	}
	public boolean getPropertyAsBoolean(String name, boolean def)
	{
		Object obj = dataMap == null ? null : dataMap.getProperty(name);
		return obj == null ? def : Convert.toBoolean(obj);
	}
	public String getPropertyAsString(String name, String def)
	{
		Object obj = dataMap == null ? null : dataMap.getProperty(name);
		return obj == null ? def : Convert.toString(obj);
	}
	public Object getProperty(String name, Object def)
	{
		Object obj = dataMap == null ? null : dataMap.getProperty(name);
		return obj == null ? def : obj;
	}
	public void attach(IDataMap<String, Object> dataMap) {
		this.dataMap = dataMap;
	}
}
