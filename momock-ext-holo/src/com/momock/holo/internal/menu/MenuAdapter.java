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
package com.momock.holo.internal.menu;

import java.util.List;

import android.content.Context;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.momock.ext.holo.R;

public final class MenuAdapter extends ArrayAdapter<MenuItem> {

	private Menu mMenu;

	public MenuAdapter(final Context context) {
		super(context, R.layout.menu_list_item);
	}

	@Override
	public long getItemId(final int index) {
		return getItem(index).getItemId();
	}

	@Override
	public View getView(final int position, final View convertView, final ViewGroup parent) {
		final TextView view = (TextView) super.getView(position, convertView, parent);
		final MenuItem item = getItem(position);
		view.setEnabled(item.isEnabled());
		view.setVisibility(item.isVisible() ? View.VISIBLE : View.GONE);
		view.setCompoundDrawablesWithIntrinsicBounds(item.getIcon(), null, null, null);
		return view;
	}

	public void setMenu(final Menu menu) {
		mMenu = menu;
		setMenuItems();
	}

	void setMenuItems() {
		clear();
		final List<MenuItem> items = mMenu == null ? null : ((MenuImpl) mMenu).getMenuItems();
		if (items == null) return;
		for (final MenuItem item : items) {
			if (item.isVisible()) {
				add(item);
			}
		}
	}

}
