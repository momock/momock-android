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
package com.momock.util;

import com.momock.data.DataList;
import com.momock.data.DataMap;
import com.momock.data.IDataMutableList;
import com.momock.data.IDataNode;
import com.momock.data.IDataView;

public class DataHelper {
	public static <T extends DataMap<String, Object>> IDataMutableList<T> fromDataNodes(IDataView<IDataNode> nodes, Class<T> cls){
		DataList<T> dl = new DataList<T>();
		for(int i = 0; i < nodes.getItemCount(); i++){
			try {
				T obj = cls.newInstance();
				obj.copyPropertiesFrom(nodes.getItem(i));
				dl.addItem(obj);
			} catch (Exception e) {
				Logger.error(e.getMessage());
			}
		}
		return dl;
	}
}
