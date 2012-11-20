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

import android.app.Activity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TabHost;
import android.widget.TabWidget;

public class TabHolder implements IComponentHolder {
	TabHost tabHost;
	TabWidget tabWidget;
	ViewGroup tabContent;

	public TabHolder(TabHost tabHost, TabWidget tabWidget, ViewGroup tabContent) {
		this.tabHost = tabHost;
		this.tabWidget = tabWidget;
		this.tabContent = tabContent;
	}

	public TabHost getTabHost() {
		return tabHost;
	}

	public TabWidget getTabWidget() {
		return tabWidget;
	}

	public ViewGroup getTabContent() {
		return tabContent;
	}

	public static TabHolder get(final Activity activity) {
		return new TabHolder(
				(TabHost) activity.findViewById(android.R.id.tabhost),
				(TabWidget) activity.findViewById(android.R.id.tabs),
				(FrameLayout) activity.findViewById(android.R.id.tabcontent));
	}
	public static TabHolder get(final Activity activity, int realTabContentId) {
		return new TabHolder(
				(TabHost) activity.findViewById(android.R.id.tabhost),
				(TabWidget) activity.findViewById(android.R.id.tabs),
				(ViewGroup) activity.findViewById(realTabContentId));
	}
}
