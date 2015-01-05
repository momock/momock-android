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

import android.app.Activity;
import android.app.Fragment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;
import android.widget.TabWidget;

import com.momock.outlet.tab.ITabPlug;
import com.momock.util.Logger;

public abstract class TabHolder implements IComponentHolder {
	public interface OnCreateTabIndicatorHandler{
		View onCreateTabIndicator(ITabPlug plug);
	}
	OnCreateTabIndicatorHandler handler = null;
	public void setOnCreateTabIndicatorHandler(OnCreateTabIndicatorHandler handler){
		this.handler = handler;
	}
	public void setTabIndicator(TabHost.TabSpec spec, ITabPlug plug){
		if (this.handler != null)
			spec.setIndicator(handler.onCreateTabIndicator(plug));
		else if (plug.getIcon() == null)
			spec.setIndicator(plug.getText().getText());
		else
			spec.setIndicator(plug.getText().getText(), plug.getIcon().getAsDrawable());
	}
	
	public abstract TabHost getTabHost();

	public TabWidget getTabWidget() {
		return getTabHost().getTabWidget();
	}

	public ViewGroup getTabContent() {
		return getTabHost().getTabContentView();
	}

	public static TabHolder get(View container) {
		Logger.check(container != null, "Parameter container cannot be null!");
		final WeakReference<View> refContainer = new WeakReference<View>(
				container);
		return new TabHolder() {
			
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

	public static TabHolder get(View container, final int realTabContentId) {
		Logger.check(container != null, "Parameter container cannot be null!");
		final WeakReference<View> refContainer = new WeakReference<View>(
				container);
		return new TabHolder() {
			protected WeakReference<ViewGroup> tabContent = null;

			@Override
			public ViewGroup getTabContent() {
				if (tabContent == null || tabContent.get() == null)
					tabContent = new WeakReference<ViewGroup>((ViewGroup) getTabHost()
							.findViewById(realTabContentId));
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

	public static TabHolder get(Activity activity) {
		Logger.check(activity != null, "Parameter activity cannot be null!");
		final WeakReference<Activity> refActivity = new WeakReference<Activity>(activity);
		return new TabHolder() {
			
			@Override
			public TabHost getTabHost() {
				Logger.check(refActivity.get() != null,
						"The activity of TabHost has not been available!");
				return (TabHost) refActivity.get().findViewById(android.R.id.tabhost);
			}

		};
	}

	public static TabHolder get(Activity activity, final int realTabContentId) {
		Logger.check(activity != null, "Parameter activity cannot be null!");
		final WeakReference<Activity> refActivity = new WeakReference<Activity>(activity);
		return new TabHolder() {
			protected WeakReference<ViewGroup> tabContent = null;

			@Override
			public ViewGroup getTabContent() {
				if (tabContent == null || tabContent.get() == null)
					tabContent = new WeakReference<ViewGroup>((ViewGroup) getTabHost()
							.findViewById(realTabContentId));
				return tabContent.get();
			}
			
			@Override
			public TabHost getTabHost() {
				Logger.check(refActivity.get() != null,
						"The activity of TabHost has not been available!");
				return (TabHost) refActivity.get().findViewById(android.R.id.tabhost);
			}

		};
	}

	public static TabHolder get(Fragment container) {
		return get(container.getView());
	}

	public static TabHolder get(Fragment container, int realTabContentId) {
		return get(container.getView(), realTabContentId);
	}

}
