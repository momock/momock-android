/*******************************************************************************
 * Copyright 2013 momock.com
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

import android.os.Bundle;
import android.os.Parcel;
import android.util.Base64;

public class BundleHelper {
	public static String toString(Bundle in) {
		if (in == null) return null;
		Parcel parcel = Parcel.obtain();
		String serialized = null;
		try {
			in.writeToParcel(parcel, 0);
			serialized = Base64.encodeToString(parcel.marshall(), 0);
		} catch (Exception e) {
			Logger.error(e);
		} finally {
			parcel.recycle();
		}
		return serialized;
	}

	public static Bundle fromString(String serialized) {
		Bundle bundle = null;
		if (serialized != null) {
			Parcel parcel = Parcel.obtain();
			try {
				byte[] data = Base64.decode(serialized, 0);
				parcel.unmarshall(data, 0, data.length);
				parcel.setDataPosition(0);
				bundle = parcel.readBundle();
			} finally {
				parcel.recycle();
			}
		}
		return bundle;
	}
}
