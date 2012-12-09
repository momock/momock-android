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
import com.momock.data.IDataList;
import com.momock.data.IDataMap;
import com.momock.data.IDataMutableMap;

public class DataHelper {

	@SuppressWarnings("unchecked")
	public static <T, I extends IDataMap<String, Object>> DataList<T> getBeanList(IDataList<I> nodes, Class<T> beanClass){
		DataList<T> dl = new DataList<T>();
		for(int i = 0; i < nodes.getItemCount(); i++){
			try {
				T obj = beanClass.newInstance();
				if (obj instanceof IDataMutableMap){
					IDataMutableMap<String, Object> target = (IDataMutableMap<String, Object>)obj;
					target.copyPropertiesFrom(nodes.getItem(i));
				} else {
					BeanHelper.copyPropertiesFromDataMap(obj, nodes.getItem(i));					
				}					
				dl.addItem(obj);
			} catch (Exception e) {
				Logger.error(e.getMessage());
			}
		}
		return dl;
	}

	@SuppressWarnings("unchecked")
	public static <T, I extends IDataList<?>> DataList<T> getBeanList(IDataList<I> nodes, Class<T> beanClass, String ... props){
		DataList<T> dl = new DataList<T>();
		for(int i = 0; i < nodes.getItemCount(); i++){
			try {
				T obj = beanClass.newInstance();
				if (obj instanceof IDataMutableMap){
					IDataMutableMap<String, Object> target = (IDataMutableMap<String, Object>)obj;
					for(int j = 0; j < props.length; j++){
						target.setProperty(props[j], nodes.getItem(i).getItem(j));
					}		
				} else {
					for(int j = 0; j < props.length; j++){
						BeanHelper.setProperty(obj, props[j], nodes.getItem(i).getItem(j));
					}								
				}					
				dl.addItem(obj);
			} catch (Exception e) {
				Logger.error(e.getMessage());
			}
		}
		return dl;
	}
}
