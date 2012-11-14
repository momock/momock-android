package com.momock.util;

import java.io.IOException;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class XmlHelper {

    public static void skipCurrentTag(XmlPullParser parser)
            throws XmlPullParserException, IOException {
        int outerDepth = parser.getDepth();
        int type = parser.getEventType();
    	if (type != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        while ((type=parser.next()) != XmlPullParser.END_DOCUMENT
               && (type != XmlPullParser.END_TAG
                       || parser.getDepth() > outerDepth)) {
        }
    }
    
}
