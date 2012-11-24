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
package com.momock.holo.app;

import com.momock.holo.internal.actionbar.ActionBarCompat;
import com.momock.holo.internal.actionbar.ActionBarCompatBase;
import com.momock.holo.internal.actionbar.ActionBarCompatNative;
import com.momock.holo.internal.actionbar.MethodsCompat;

import android.app.Activity;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;

public class ActionBarPreferenceActivity extends PreferenceActivity {

	ActionBarCompat mActionBarCompat = ActionBarCompat.getInstance(this);
	private boolean mActionBarInitialized = false;

	@Override
	public MenuInflater getMenuInflater() {
		return mActionBarCompat.getMenuInflater(super.getMenuInflater());
	}

	public ActionBar getSupportActionBar() {
		return mActionBarCompat.getActionBar();
	}

	public void invalidateSupportOptionsMenu() {
		if (mActionBarCompat instanceof ActionBarCompatNative) {
			MethodsCompat.invalidateOptionsMenu(this);
		} else if (mActionBarCompat instanceof ActionBarCompatBase) {
			((ActionBarCompatBase) mActionBarCompat).invalidateOptionsMenu();
		}
	}

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		if (mActionBarCompat instanceof ActionBarCompatBase) {
			((ActionBarCompatBase) mActionBarCompat).requestCustomTitleView();
			super.onCreate(savedInstanceState);
			((ActionBarCompatBase) mActionBarCompat).setCustomTitleView();
		} else {
			super.onCreate(savedInstanceState);
		}
	}

	/**
	 * Base action bar-aware implementation for
	 * {@link Activity#onCreateOptionsMenu(android.view.Menu)}.
	 * 
	 * Note: marking menu items as invisible/visible is not currently supported.
	 */
	@Override
	public boolean onCreateOptionsMenu(final Menu menu) {
		boolean retValue = super.onCreateOptionsMenu(menu);
		if (mActionBarCompat instanceof ActionBarCompatBase) {
			retValue = true;
		}
		return retValue;
	}

	@Override
	public void onPostCreate(final Bundle savedInstanceState) {
		if (mActionBarCompat instanceof ActionBarCompatBase && !mActionBarInitialized) {
			mActionBarInitialized = ((ActionBarCompatBase) mActionBarCompat).setCustomTitleView();
		}
		super.onPostCreate(savedInstanceState);
	}

	@Override
	public boolean onPrepareOptionsMenu(final Menu menu) {
		super.onPrepareOptionsMenu(menu);
		if (mActionBarCompat instanceof ActionBarCompatBase) {
			((ActionBarCompatBase) mActionBarCompat).hideInRealMenu(menu);
		}
		return true;
	}

	@Override
	public void onTitleChanged(final CharSequence title, final int color) {
		if (mActionBarCompat instanceof ActionBarCompatBase) {
			getSupportActionBar().setTitle(title);
		}
		super.onTitleChanged(title, color);
	}

	public void requestSupportWindowFeature(final int featureId) {
		if (mActionBarCompat instanceof ActionBarCompatNative) {
			requestWindowFeature(featureId);
		} else {
			switch (featureId) {
				case Window.FEATURE_INDETERMINATE_PROGRESS: {
					if (mActionBarCompat instanceof ActionBarCompatBase) {
						((ActionBarCompatBase) mActionBarCompat).setProgressBarIndeterminateEnabled(true);
					}
				}
			}
		}
	}

	@Override
	public void setContentView(final int layoutResID) {
		super.setContentView(layoutResID);
		if (mActionBarCompat instanceof ActionBarCompatBase && !mActionBarInitialized) {
			mActionBarInitialized = ((ActionBarCompatBase) mActionBarCompat).setCustomTitleView();
		}
	}

	@Override
	public void setContentView(final View view) {
		super.setContentView(view);
		if (mActionBarCompat instanceof ActionBarCompatBase && !mActionBarInitialized) {
			mActionBarInitialized = ((ActionBarCompatBase) mActionBarCompat).setCustomTitleView();
		}
	}

	@Override
	public void setContentView(final View view, final LayoutParams params) {
		super.setContentView(view, params);
		if (mActionBarCompat instanceof ActionBarCompatBase && !mActionBarInitialized) {
			mActionBarInitialized = ((ActionBarCompatBase) mActionBarCompat).setCustomTitleView();
		}
	}

	public void setSupportProgressBarIndeterminateVisibility(final boolean visible) {
		if (mActionBarCompat instanceof ActionBarCompatBase) {
			((ActionBarCompatBase) mActionBarCompat).setProgressBarIndeterminateVisibility(visible);
		} else {
			setProgressBarIndeterminateVisibility(visible);
		}
	}

}
