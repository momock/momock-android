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

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

public class XmlHelper {
	static XmlPullParserFactory fac = null;
	static {
		try {
			fac = XmlPullParserFactory.newInstance();
		} catch (XmlPullParserException e) {
			Logger.error(e);
		}
	}

	public static XmlPullParser createParser(String xml) {
		Logger.check(fac != null,
				"XmlPullParserFactory has not yet been created!");
		try {
			XmlPullParser parser = fac.newPullParser();
			parser.setInput(new StringReader(xml));
			return parser;
		} catch (XmlPullParserException e) {
			Logger.error(e);
		}
		return null;
	}

	public static XmlPullParser createParser(InputStream in, String encode) {
		Logger.check(fac != null,
				"XmlPullParserFactory has not yet been created!");
		try {
			XmlPullParser parser = fac.newPullParser();
			parser.setInput(in, encode);
			return parser;
		} catch (XmlPullParserException e) {
			Logger.error(e);
		}
		return null;
	}

	public static void skipCurrentTag(XmlPullParser parser)
			throws XmlPullParserException, IOException {
		int outerDepth = parser.getDepth();
		int type = parser.getEventType();
		if (type != XmlPullParser.START_TAG) {
			throw new IllegalStateException();
		}
		while ((type = parser.next()) != XmlPullParser.END_DOCUMENT
				&& (type != XmlPullParser.END_TAG || parser.getDepth() > outerDepth)) {
		}
	}
}