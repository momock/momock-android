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
package com.momock.holo.internal.actionbar;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;

public final class MethodsCompat {

	private MethodsCompat() {
		throw new IllegalArgumentException("You cannot create instance for this class");
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public static void invalidateOptionsMenu(final Activity activity) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			activity.invalidateOptionsMenu();
		}
	}

}
