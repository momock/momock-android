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
package com.momock.outlet.tab;

import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;
import android.widget.TabHost.TabContentFactory;
import android.widget.TabWidget;

import com.momock.data.IDataList;
import com.momock.holder.FragmentHolder;
import com.momock.holder.FragmentTabHolder;
import com.momock.outlet.IPlug;
import com.momock.outlet.Outlet;
import com.momock.util.Logger;

public class FragmentPagerTabOutlet extends Outlet implements ITabOutlet{
	IDataList<IPlug> plugs;
	FragmentTabHolder target;
	public void attach(final FragmentTabHolder tabHolder) {
		Logger.check(tabHolder != null, "Parameter tabHolder cannot be null!");
		this.target = tabHolder;
		final TabHost tabHost = target.getTabHost();
		plugs = getPlugs();
		Logger.check(target.getTabContent() instanceof ViewPager,
				"The tab content container must be a ViewPager!");
		final ViewPager tabContent = (ViewPager) target.getTabContent();
		tabContent.setOffscreenPageLimit(plugs.getItemCount());
		tabContent.setAdapter(new PagerAdapter() {
			FragmentHolder primary = null;

			@Override
			public int getCount() {
				return plugs.getItemCount();
			}

			@Override
			public Object instantiateItem(ViewGroup container, int position) {
				return (FragmentHolder) ((ITabPlug)plugs.getItem(position)).getContent();
			}

			@Override
			public boolean isViewFromObject(View view, Object object) {
				return true;
			}

			@Override
			public void setPrimaryItem(ViewGroup container, final int position,
					final Object object) {

				new Handler().post(new Runnable() {

					@Override
					public void run() {
						FragmentHolder fh = (FragmentHolder) object;
						Logger.debug("setPrimaryItem (" + position + ")"
								+ (fh != primary));
						FragmentManager fm = target.getFragmentManager();
						if (fm != null && fh != primary) {
							FragmentTransaction ft = fm.beginTransaction();
							ft.replace(target.getTabContentId(), fh.getFragment());
							ft.commit();
							fm.executePendingTransactions();
							primary = fh;
						}
					}
				});
			}

		});
		tabContent
				.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

					@Override
					public void onPageSelected(int position) {
						TabWidget widget = tabHost.getTabWidget();
						int oldFocusability = widget
								.getDescendantFocusability();
						widget.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
						tabHost.setCurrentTab(position);
						widget.setDescendantFocusability(oldFocusability);
					}

					@Override
					public void onPageScrolled(int position,
							float positionOffset, int positionOffsetPixels) {

					}

					@Override
					public void onPageScrollStateChanged(int state) {

					}
				});
		tabHost.setup();
		tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {

			@Override
			public void onTabChanged(String tabId) {
				int index = tabHost.getCurrentTab();
				ITabPlug plug = (ITabPlug)plugs.getItem(index);
				setActivePlug(plug);
				tabContent.setCurrentItem(index, true);
			}
		});
		for (int i = 0; i < plugs.getItemCount(); i++) {
			final ITabPlug plug = (ITabPlug)plugs.getItem(i);
			Logger.check(plug.getContent() instanceof FragmentHolder,
					"Plug in PagerTabOutlet must contains a FragmentHolder content!");

			TabHost.TabSpec spec = tabHost.newTabSpec("" + i);
			target.setTabIndicator(spec, plug);
			spec.setContent(new TabContentFactory() {

				@Override
				public View createTabContent(String tag) {
					View v = new View(tabHost.getContext());
					v.setMinimumWidth(0);
					v.setMinimumHeight(0);
					return v;
				}

			});
			tabHost.addTab(spec);
			if (getActivePlug() == plug)
				tabHost.setCurrentTab(i);
		}
	}

	@Override
	public void onActivate(IPlug plug) {
		if (((ITabPlug)plug).getContent() != null) {
			TabHost tabHost = target.getTabHost();
			tabHost.setCurrentTab(getIndexOf(plug));
		} else {
			Logger.debug("The plug of FragmentPagerTabOutlet has not been attached !");
		}
	}
}
