package com.momock.util;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.xmlpull.v1.XmlPullParser;

public class BeanHelper {
	static HashMap<Class<?>, HashMap<String, Method>> propsCache = new HashMap<Class<?>, HashMap<String, Method>>();
	@SuppressWarnings("rawtypes")
	public static void copyPropertiesFromMap(Object obj, Map propsToCopy) {
		Class<?> beanClass = obj.getClass();
		HashMap<String, Method> props;
		if (propsCache.containsKey(beanClass))
		{
			props = propsCache.get(beanClass);			
		}
		else
		{
			props = new HashMap<String, Method>();
			propsCache.put(beanClass, props);
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
