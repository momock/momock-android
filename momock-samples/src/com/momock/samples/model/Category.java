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
package com.momock.samples.model;

import com.momock.data.DataMap;
import com.momock.util.Convert;

public class Category extends DataMap<String, Object> {

	public static final String Id = "Id";
	public static final String Name = "Name";

	public Integer getId() {
		return Convert.toInteger(this.getProperty(Id));
	}

	public void setId(int id) {
		this.setProperty(Id, id);
	}

	public String getName() {
		return (String) this.getProperty(Name);
	}

	public void setName(String name) {
		this.setProperty(Name, name);
	}
	
	public String getIconUri(){
		//String uri = "file:///android_asset/icons/category/" + (getId() % 5) + ".png";
		String uri = "assets://icons/category/" + (getId() % 5) + ".png";
		uri = "https://www.google.com/images/srpr/logo3w.png";
		return uri;
	}
}
