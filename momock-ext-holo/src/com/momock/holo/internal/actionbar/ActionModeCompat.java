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


import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.momock.ext.holo.R;
import com.momock.holo.internal.actionbar.MenuBar.OnMenuItemClickListener;
import com.momock.holo.view.ActionMode;

public class ActionModeCompat extends ActionMode {

	private final Callback mCallbackProxy;
	private final View mActionModeContainer;

	private final TextView mTitleView, mSubtitleView;
	private final MenuBar mMenuBar;
	private final ActionBarCompatBase mActionBar;
	private final OnMenuItemClickListener mListener = new OnMenuItemClickListener() {

		@Override
		public boolean onMenuItemClick(final MenuItem item) {
			if (mCallbackProxy != null) return mCallbackProxy.onActionItemClicked(ActionModeCompat.this, item);
			return false;
		}

	};

	public ActionModeCompat(final ActionBarCompatBase action_bar, final Callback callback) {
		mCallbackProxy = callback;
		mActionBar = action_bar;
		mActionModeContainer = action_bar.startActionMode();
		mActionModeContainer.findViewById(R.id.action_mode_cancel).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(final View v) {
				finish();
			}

		});
		mTitleView = (TextView) mActionModeContainer.findViewById(R.id.action_mode_title);
		mSubtitleView = (TextView) mActionModeContainer.findViewById(R.id.action_mode_subtitle);
		mMenuBar = (MenuBar) mActionModeContainer.findViewById(R.id.action_mode_menu);
		mMenuBar.setOnMenuItemClickListener(mListener);
		if (mCallbackProxy != null) {
			final Menu menu = mMenuBar.getMenu();
			if (mCallbackProxy.onCreateActionMode(this, menu) && mCallbackProxy.onPrepareActionMode(this, menu)) {
				mMenuBar.show();
			}
		}
	}

	@Override
	public void finish() {
		mActionBar.stopActionMode();
		if (mCallbackProxy != null) {
			mCallbackProxy.onDestroyActionMode(this);
		}
	}

	@Override
	public Menu getMenu() {
		if (mMenuBar == null) return null;
		return mMenuBar.getMenu();
	}

	@Override
	public MenuInflater getMenuInflater() {
		if (mMenuBar == null) return null;
		return mMenuBar.getMenuInflater();
	}

	@Override
	public CharSequence getSubtitle() {
		if (mSubtitleView == null) return null;
		return mSubtitleView.getText();
	}

	@Override
	public CharSequence getTitle() {
		if (mTitleView == null) return null;
		return mTitleView.getText();
	}

	@Override
	public void invalidate() {
		if (mMenuBar == null || mActionModeContainer == null) return;
		mActionModeContainer.invalidate();
		mMenuBar.show();
	}

	@Override
	public void setSubtitle(final CharSequence subtitle) {
		if (mSubtitleView == null) return;
		mSubtitleView.setText(subtitle);
		mTitleView.setVisibility(subtitle != null ? View.VISIBLE : View.GONE);
	}

	@Override
	public void setSubtitle(final int resId) {
		if (mSubtitleView == null) return;
		mSubtitleView.setText(resId);
		mSubtitleView.setVisibility(resId != 0 ? View.VISIBLE : View.GONE);
	}

	@Override
	public void setTitle(final CharSequence title) {
		if (mTitleView == null) return;
		mTitleView.setText(title);
	}

	@Override
	public void setTitle(final int resId) {
		if (mTitleView == null) return;
		mTitleView.setText(resId);
	}

}
