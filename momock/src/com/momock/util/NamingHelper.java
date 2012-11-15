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

public class NamingHelper {
	public static final int STYLE_UNKNOWN = 0;
	public static final int STYLE_PASCAL = 1;
	public static final int STYLE_CAMEL = 2;
	public static final int STYLE_UNDERSCORE = 3;

	public static String toCamelCase(String name) {
		if (name == null)
			return name;
		switch (getNamingStyle(name)) {
		case STYLE_CAMEL:
			return name;
		case STYLE_UNDERSCORE:
			return fromUnderscoreCase(name, STYLE_CAMEL);
		}
		return name.substring(0, 1).toLowerCase() + name.substring(1);
	}

	public static String toPascalCase(String name) {
		if (name == null)
			return name;
		switch (getNamingStyle(name)) {
		case STYLE_PASCAL:
			return name;
		case STYLE_UNDERSCORE:
			return fromUnderscoreCase(name, STYLE_PASCAL);
		}
		return name.substring(0, 1).toUpperCase() + name.substring(1);
	}

	public static String toUnderscoreCase(String name) {
		if (name == null)
			return name;
		if (getNamingStyle(name) == STYLE_UNDERSCORE)
			return name;
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < name.length(); i++) {
			Character c = name.charAt(i);
			if (Character.isUpperCase(c)) {
				if (i > 0)
					sb.append("_");
				sb.append(Character.toLowerCase(c));
			} else {
				sb.append(c);
			}
		}
		return sb.toString();
	}

	public static String fromUnderscoreCase(String name, int srcStyle) {
		if (name == null)
			return null;
		switch (getNamingStyle(name)) {
		case STYLE_PASCAL:
			return name;
		case STYLE_CAMEL:
			return toPascalCase(name);
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < name.length(); i++) {
			Character c = name.charAt(i);
			if (c == '_') {
				i++;
				c = name.charAt(i);
				sb.append(Character.toUpperCase(c));
			} else {
				sb.append(i == 0 ? (srcStyle == STYLE_PASCAL ? Character
						.toUpperCase(c) : Character.toLowerCase(c)) : c);
			}
		}
		return sb.toString();
	}

	public static int getNamingStyle(String name) {
		if (name == null)
			return STYLE_UNKNOWN;
		if (name.indexOf('_') != -1)
			return STYLE_UNDERSCORE;
		return Character.isUpperCase(name.charAt(0)) ? STYLE_PASCAL
				: STYLE_CAMEL;
	}
}
