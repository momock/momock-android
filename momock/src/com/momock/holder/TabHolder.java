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

import junit.framework.Assert;
import android.app.Activity;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TabHost;
import android.widget.TabWidget;

public class TabHolder implements IComponentHolder {
	WeakReference<TabHost> tabHost;
	WeakReference<TabWidget> tabWidget;
	WeakReference<ViewGroup> tabContent;

	public TabHolder(Activity activity) {
		Assert.assertNotNull(activity);
		create((TabHost) activity.findViewById(android.R.id.tabhost),
				(TabWidget) activity.findViewById(android.R.id.tabs),
				(FrameLayout) activity.findViewById(android.R.id.tabcontent));
	}

	public TabHolder(Activity activity, int realTabContentId) {
		Assert.assertNotNull(activity);
		create((TabHost) activity.findViewById(android.R.id.tabhost),
				(TabWidget) activity.findViewById(android.R.id.tabs),
				(ViewGroup) activity.findViewById(realTabContentId));
	}

	public TabHolder(Fragment container) {
		this(container.getView());
	}

	public TabHolder(Fragment container, int realTabContentId) {
		this(container.getView(), realTabContentId);
	}
	
	public TabHolder(View container) {
		Assert.assertNotNull(container);
		create((TabHost) (container instanceof TabHost ? container
				: container.findViewById(android.R.id.tabhost)),
				(TabWidget) container.findViewById(android.R.id.tabs),
				(FrameLayout) container.findViewById(android.R.id.tabcontent));
	}

	public TabHolder(View container, int realTabContentId) {
		Assert.assertNotNull(container);
		create((TabHost) (container instanceof TabHost ? container
				: container.findViewById(android.R.id.tabhost)),
				(TabWidget) container.findViewById(android.R.id.tabs),
				(ViewGroup) container.findViewById(realTabContentId));
	}

	protected void create(TabHost tabHost, TabWidget tabWidget,
			ViewGroup tabContent) {
		Assert.assertTrue(tabHost != null && tabWidget != null && tabContent != null);
		this.tabHost = new WeakReference<TabHost>(tabHost);
		this.tabWidget = new WeakReference<TabWidget>(tabWidget);
		this.tabContent = new WeakReference<ViewGroup>(tabContent);
	}

	public TabHost getTabHost() {
		return tabHost.get();
	}

	public TabWidget getTabWidget() {
		return tabWidget.get();
	}

	public ViewGroup getTabContent() {
		return tabContent.get();
	}
}
