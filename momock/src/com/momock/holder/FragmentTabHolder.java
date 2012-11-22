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
package com.momock.holder;

import java.lang.ref.WeakReference;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;

import com.momock.util.Logger;

public abstract class FragmentTabHolder extends TabHolder {

	public abstract int getTabContentId();

	public abstract FragmentManager getFragmentManager();

	public static FragmentTabHolder get(FragmentManager fm, View container,
			final int tabContentId) {
		final WeakReference<FragmentManager> refFragmentManager = new WeakReference<FragmentManager>(
				fm);
		final WeakReference<View> refContainer = new WeakReference<View>(
				container);
		return new FragmentTabHolder() {
			protected WeakReference<ViewGroup> tabContent = null;

			@Override
			public int getTabContentId() {
				return tabContentId;
			}

			@Override
			public FragmentManager getFragmentManager() {
				return refFragmentManager.get();
			}

			@Override
			public ViewGroup getTabContent() {
				if (tabContent == null || tabContent.get() == null)
					tabContent = new WeakReference<ViewGroup>(
							(ViewGroup) getTabHost().findViewById(tabContentId));
				return tabContent.get();
			}

			@Override
			public TabHost getTabHost() {
				Logger.check(refContainer.get() != null,
						"The TabHost container has not been available!");
				View container = refContainer.get();
				return (TabHost) (container instanceof TabHost ? container
						: container.findViewById(android.R.id.tabhost));
			}

		};
	}

	public static FragmentTabHolder get(FragmentActivity activity,
			View container, int tabContentId) {
		return get(activity.getSupportFragmentManager(), container,
				tabContentId);
	}

	public static FragmentTabHolder get(final Fragment containerFragment,
			final int tabContentId) {
		return new FragmentTabHolder() {
			protected WeakReference<ViewGroup> tabContent = null;

			@Override
			public int getTabContentId() {
				return tabContentId;
			}

			@Override
			public FragmentManager getFragmentManager() {
				return containerFragment.getFragmentManager();
			}

			@Override
			public ViewGroup getTabContent() {
				if (tabContent == null || tabContent.get() == null)
					tabContent = new WeakReference<ViewGroup>(
							(ViewGroup) getTabHost().findViewById(tabContentId));
				return tabContent.get();
			}

			@Override
			public TabHost getTabHost() {
				Logger.check(containerFragment.getView() != null,
						"The TabHost container has not been available!");
				View container = containerFragment.getView();
				return (TabHost) (container instanceof TabHost ? container
						: container.findViewById(android.R.id.tabhost));
			}

		};
	}
}
