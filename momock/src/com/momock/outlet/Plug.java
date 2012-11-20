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
package com.momock.outlet;

import java.util.List;

import com.momock.data.DataMap;
import com.momock.data.IDataMutableMap;

public class Plug implements IPlug{
	protected DataMap<String, Object> properties = null;

	@Override
	public boolean hasProperty(String name) {		
		return properties == null ? false : properties.hasProperty(name);
	}

	@Override
	public Object getProperty(String name) {
		return properties == null ? null :  properties.getProperty(name);
	}

	@Override
	public List<String> getPropertyNames() {
		return properties == null ? null : properties.getPropertyNames();
	}

	@Override
	public void setProperty(String name, Object val) {
		if (properties == null)
			properties = new DataMap<String, Object>();
		properties.setProperty(name, val);
	}
}
