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

import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.momock.data.DataNode;
import com.momock.data.IDataNode;

public class JsonHelper {
	static void build(DataNode dn, Object obj){
		if (obj instanceof JSONObject){
			JSONObject jobj = (JSONObject)obj;
			Iterator<?> keys = jobj.keys();
	        while( keys.hasNext() ){
	            String key = (String)keys.next();
	            Object val;
				try {
					val = jobj.get(key);
				} catch (JSONException e) {
					Logger.error(e.getMessage());
					continue;
				}
	            if(val instanceof JSONObject || val instanceof JSONArray){
	            	DataNode cdn = new DataNode();
	            	build(cdn, val);
	            	val = cdn;
	            }
	            dn.setProperty(key, val);
	        }
		} else if (obj instanceof JSONArray){
			JSONArray jarr = (JSONArray)obj;
			for(int i = 0; i < jarr.length(); i++){
				Object val;
				try {
					val = jarr.get(i);
				} catch (JSONException e) {
					Logger.error(e.getMessage());
					continue;
				}
				if(val instanceof JSONObject || val instanceof JSONArray){
	            	DataNode cdn = new DataNode();
	            	build(cdn, val);
	            	val = cdn;
	            }
				dn.addItem(val);
			}
		} else {
			
		}
	}
	public static IDataNode parse(String json) {
		JSONTokener tokener = new JSONTokener(json);
		Object root;
		try {
			root = tokener.nextValue();
		} catch (JSONException e) {
			Logger.error(e.getMessage());
			return null;
		}
		DataNode dn = new DataNode();
		build(dn, root);
		return dn;
	}

}
