/*******************************************************************************
 * Copyright 2015 momock.com
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

import com.momock.util.BeanHelper;

public class ValueBinderSelector implements IBinderSelector {
	String propName;
	Object val;
	public ValueBinderSelector(String propName, Object val){
		this.propName = propName;
		this.val = val;
	}
	@Override
	public boolean onSelect(Object item) {
		Object propVal = null;
		if (item != null && propName != null)
			propVal = BeanHelper.getProperty(item, propName, null);			
		return val == null ? propVal == null : val.equals(propVal);
	}		
}