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

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

public class JsonHelper {
	public static Object select(JSONObject node, String path){
		if (path == null) return node;
		int pos = path.indexOf("/");
		String current = pos == -1 ? path : path.substring(0, pos);
		String next = pos == -1 ? null : path.substring(pos + 1);
		Iterator<?> keys = node.keys();
        while( keys.hasNext() ){
            String key = (String)keys.next();
            if (current.equals(key)){
	            Object val;
				try {
					val = node.get(key);
					if (next == null) return val;
		            if(val instanceof JSONObject){
		            	return select((JSONObject)val, next);
		            } else {
		            	return null;
		            }
				} catch (JSONException e) {
					Logger.error(e);
				}
            }
        }
        return null;
	}
	public static String selectString(JSONObject node, String path){
		return Convert.toString(select(node, path));
	}
	public static Integer selectInteger(JSONObject node, String path){
		return Convert.toInteger(select(node, path));
	}
	public static JSONObject parse(String json) {
		JSONTokener tokener = new JSONTokener(json);
		Object root;
		try {
			root = tokener.nextValue();
		} catch (JSONException e) {
			Logger.error(e);
			return null;
		}
		return (JSONObject)root;
	}

}
