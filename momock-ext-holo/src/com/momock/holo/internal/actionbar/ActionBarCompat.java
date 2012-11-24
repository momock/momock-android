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

import com.momock.holo.app.ActionBar;

import android.app.Activity;
import android.os.Build;
import android.view.MenuInflater;

public abstract class ActionBarCompat {

	public ActionBar getActionBar() {
		if (this instanceof ActionBar) return (ActionBar) this;
		return null;
	}

	/**
	 * Returns a {@link MenuInflater} for use when inflating menus. The
	 * implementation of this method in {@link ActionBarHelperBase} returns a
	 * wrapped menu inflater that can read action bar metadata from a menu
	 * resource pre-ICS.
	 */
	public MenuInflater getMenuInflater(final MenuInflater inflater) {
		return inflater;
	}

	public static ActionBarCompat getInstance(final Activity activity) {
		if (activity == null) return null;

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
			return new ActionBarCompatNative(activity);
		else
			return new ActionBarCompatBase(activity);
	}

}
