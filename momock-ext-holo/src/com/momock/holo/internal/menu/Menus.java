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

import java.util.ArrayList;
import java.util.Collection;

import android.view.MenuItem;

@SuppressWarnings("serial")
final class Menus extends ArrayList<MenuItem> {

	private final MenuAdapter mAdapter;

	public Menus() {
		this(null);
	}

	public Menus(final MenuAdapter adapter) {
		mAdapter = adapter;
	}

	@Override
	public void add(final int index, final MenuItem object) {
		super.add(index, object);
		if (mAdapter != null) {
			mAdapter.setMenuItems();
		}
	}

	@Override
	public boolean add(final MenuItem object) {
		add(findInsertIndex(object.getOrder()), object);
		return true;
	}

	@Override
	public boolean addAll(final Collection<? extends MenuItem> collection) {
		final boolean result = super.addAll(collection);
		if (mAdapter != null) {
			mAdapter.setMenuItems();
		}
		return result;
	}

	@Override
	public boolean addAll(final int index, final Collection<? extends MenuItem> collection) {
		final boolean result = super.addAll(index, collection);
		if (mAdapter != null) {
			mAdapter.setMenuItems();
		}
		return result;
	}

	@Override
	public void clear() {
		if (mAdapter != null) {
			mAdapter.setMenuItems();
		}
		super.clear();
	}

	@Override
	public MenuItem remove(final int index) {
		final MenuItem result = super.remove(index);
		if (mAdapter != null) {
			mAdapter.setMenuItems();
		}
		return result;
	}

	@Override
	public boolean remove(final Object object) {
		final boolean result = super.remove(object);
		if (mAdapter != null) {
			mAdapter.setMenuItems();
		}
		return result;
	}

	@Override
	public MenuItem set(final int index, final MenuItem object) {
		final MenuItem result = super.set(index, object);
		if (mAdapter != null) {
			mAdapter.setMenuItems();
		}
		return result;
	}

	private int findInsertIndex(final int order) {
		for (int i = size() - 1; i >= 0; i--) {
			final MenuItem item = get(i);
			if (item.getOrder() <= order) return i + 1;
		}
		return 0;
	}

}
