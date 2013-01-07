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

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.xmlpull.v1.XmlPullParser;

import com.momock.data.IDataMap;

public class BeanHelper {
	static HashMap<Class<?>, HashMap<String, Method>> setPropsCache = new HashMap<Class<?>, HashMap<String, Method>>();
	static HashMap<Class<?>, HashMap<String, Method>> getPropsCache = new HashMap<Class<?>, HashMap<String, Method>>();
	static Map<String, Method> getSetPropertyMethods(Class<?> beanClass){
		HashMap<String, Method> props;
		if (setPropsCache.containsKey(beanClass))
		{
			props = setPropsCache.get(beanClass);			
		}
		else
		{
			props = new HashMap<String, Method>();
			setPropsCache.put(beanClass, props);
			Method[] ms = beanClass.getMethods();
			for(int i = 0; i < ms.length; i++)
			{
				Method m = ms[i];
				String name = m.getName();
				if (name.startsWith("set") && name.length() > 3 && Character.isUpperCase(name.charAt(3)) && m.getParameterTypes().length == 1)
				{
					name = name.substring(3);
					props.put(name, m);
				}
			}
		}	
		return props;
	}
	static Map<String, Method> getGetPropertyMethods(Class<?> beanClass){
		HashMap<String, Method> props;
		if (getPropsCache.containsKey(beanClass))
		{
			props = getPropsCache.get(beanClass);			
		}
		else
		{
			props = new HashMap<String, Method>();
			getPropsCache.put(beanClass, props);
			Method[] ms = beanClass.getMethods();
			for(int i = 0; i < ms.length; i++)
			{
				Method m = ms[i];
				String name = m.getName();
				if (name.startsWith("get") && name.length() > 3 && Character.isUpperCase(name.charAt(3)))
				{
					name = name.substring(3);
					props.put(name, m);
				}
			}
		}	
		return props;
	}
	public static Object getProperty(Object obj, String name, Object def)
	{
		Object val = null;
		Map<String, Method> props = getGetPropertyMethods(obj.getClass());
		String propName = NamingHelper.toPascalCase(name);
		Method m = (Method)props.get(propName);		
		if (m == null) return def;
		m.setAccessible(true);
		try {
			val = m.invoke(obj);
		} catch (Exception e) {
			Logger.error(e);
		}
		return val == null ? def : val;
	}
	public static Class<?> getPropertyType(Object obj, String name){
		Map<String, Method> props = getGetPropertyMethods(obj.getClass());
		String propName = NamingHelper.toPascalCase(name);
		Method m = (Method)props.get(propName);		
		if (m != null)
			return m.getReturnType();
		else
		{
			props = getSetPropertyMethods(obj.getClass());
			m = (Method)props.get(propName);	
			if (m != null)
				return m.getParameterTypes()[0];
		}
		return null;
	}
	public static void setProperty(Object obj, String name, Object val)
	{
		Map<String, Method> props = getSetPropertyMethods(obj.getClass());
		String propName = NamingHelper.toPascalCase(name);
		Method m = (Method)props.get(propName);		
		if (m == null) return;
		m.setAccessible(true);	
		try {
			m.invoke(obj, val);
		} catch (Exception e) {
			Logger.error(e);
		}
	}
	@SuppressWarnings("rawtypes")
	public static void copyPropertiesFromMap(Object obj, Map propsToCopy) {
		Map<String, Method> props = getSetPropertyMethods(obj.getClass());
		for (Object key : propsToCopy.keySet()) {
			String propName = NamingHelper.toPascalCase(key.toString());
			Method m = (Method)props.get(propName);			
			Class<?> type = m.getParameterTypes()[0];
			m.setAccessible(true);
			Object val = propsToCopy.get(key);
			if (val != null) {
				if (Integer.class == type || int.class == type) {
					val = Convert.toInteger(val);
				} else if (Long.class == type || long.class == type) {
					val = Convert.toLong(val);
				} else if (Double.class == type || double.class == type) {
					val = Convert.toDouble(val);
				} else if (String.class == type) {
					val = Convert.toString(val);
				}
				try {
					m.invoke(obj, val);
				} catch (Exception ex) {
					Logger.debug(ex.getMessage());
				}
			}
		}
	}
	public static void copyPropertiesFromDataMap(Object obj, IDataMap<String, Object> propsToCopy) {
		Map<String, Method> props = getSetPropertyMethods(obj.getClass());
		for (String key : propsToCopy.getPropertyNames()) {
			String propName = NamingHelper.toPascalCase(key);
			Method m = (Method)props.get(propName);			
			Class<?> type = m.getParameterTypes()[0];
			m.setAccessible(true);
			Object val = propsToCopy.getProperty(key);
			if (val != null) {
				if (Integer.class == type || int.class == type) {
					val = Convert.toInteger(val);
				} else if (Long.class == type || long.class == type) {
					val = Convert.toLong(val);
				} else if (Double.class == type || double.class == type) {
					val = Convert.toDouble(val);
				} else if (String.class == type) {
					val = Convert.toString(val);
				}
				try {
					m.invoke(obj, val);
				} catch (Exception ex) {
					Logger.debug(ex.getMessage());
				}
			}
		}
	}
	public static void copyPropertiesFromXmlAttributes(Object obj, XmlPullParser parser)
	{
		int count = parser.getAttributeCount();
		HashMap<String, String> map = new HashMap<String, String>();
		for(int i = 0; i < count; i++)
		{
			String name = parser.getAttributeName(i);
			String val = parser.getAttributeValue(i);
			map.put(name, val);
		}
		copyPropertiesFromMap(obj, map);
	}
}
