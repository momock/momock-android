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

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import com.momock.data.DataList;
import com.momock.data.DataNode;
import com.momock.data.IDataList;
import com.momock.data.IDataMap;
import com.momock.data.IDataMutableMap;
import com.momock.data.IDataNode;

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
				Logger.error(e);
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
				Logger.error(e);
			}
		}
		return dl;
	}
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
					Logger.error(e);
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
					Logger.error(e);
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
	public static IDataNode parseJson(String json) {
		JSONTokener tokener = new JSONTokener(json);
		Object root;
		try {
			root = tokener.nextValue();
		} catch (JSONException e) {
			Logger.error(e);
			return null;
		}
		DataNode dn = new DataNode();
		build(dn, root);
		return dn;
	}

	public static IDataNode parseXml(String xml) {
		return parseXml(XmlHelper.createParser(xml));
	}

	public static IDataNode parseXml(InputStream is) {
		return parseXml(is, "UTF-8");
	}
	public static IDataNode parseXml(InputStream is, String encoding) {
		return parseXml(XmlHelper.createParser(is, encoding));
	}
	private static void copyProperties(XmlPullParser parser, IDataNode node) {
		for (int i = 0; i < parser.getAttributeCount(); i++) {
			node.setProperty(parser.getAttributeName(i),
					parser.getAttributeValue(i));
		}
	}

	public static IDataNode parseXml(XmlPullParser parser) {
		int type;
		IDataNode root = null;
		try {
			type = parser.nextTag();
			if (type != XmlPullParser.END_DOCUMENT) {
				root = new DataNode(parser.getName());
				IDataNode current = root;
				copyProperties(parser, current);
				for (type = parser.next(); type != XmlPullParser.END_DOCUMENT; type = parser
						.next()) {
					if (type == XmlPullParser.START_TAG) {
						String name = parser.getName().trim();
						IDataNode node = new DataNode(name, current);
						copyProperties(parser, node);
						current.addItem(node);
						current = node;
					} else if (type == XmlPullParser.END_TAG) {
						current = current.getParent();
					} else if (type == XmlPullParser.TEXT) {
						String text = parser.getText().trim();
						if (text.length() > 0) {
							IDataNode node = new DataNode(null, current);
							node.setValue(text);
							current.addItem(node);
						}
					}
				}
			}
		} catch (Exception e) {
			Logger.error(e);
		}
		return root;
	}
}
