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

import android.support.v4.app.Fragment;
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
import com.momock.holder.ViewHolder;
import com.momock.outlet.Outlet;
import com.momock.util.Logger;

public class FragmentPagerTabOutlet extends Outlet<ITabPlug, FragmentTabHolder> {
	IDataList<ITabPlug> plugs;
	@Override
	public void onAttach(final FragmentTabHolder target) {
		Logger.check(target != null, "Parameter target cannot be null!");
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
				return (FragmentHolder) plugs.getItem(position).getContent();
			}

			@Override
			public boolean isViewFromObject(View view, Object object) {
				if (!((FragmentHolder) object).isCreated())
					return false;
				return ((FragmentHolder) object).getFragment().getView() == view;
			}

			@Override
			public void setPrimaryItem(ViewGroup container, int position,
					Object object) {
				FragmentHolder fh = (FragmentHolder) object;
				Logger.debug("setPrimaryItem (" + position + ")" + (fh != primary));
				if (fh != primary) {

					FragmentManager fm = target.getFragmentManager();
					FragmentTransaction ft = fm.beginTransaction();

					if (primary != null) {
						ft.detach(primary.getFragment());
					} else {
						Fragment f = target.getFragmentManager().findFragmentById(target.getTabContentId());
						if (f != null) ft.detach(f);
					}

					if (!fh.isCreated())
						ft.add(target.getTabContentId(), fh.getFragment());
					else
						ft.attach(fh.getFragment());
					primary = fh;

					ft.commit();
				}

			}

		});
		tabContent.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

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
				tabContent.setCurrentItem(index, true);
			}
		});
		for (int i = 0; i < plugs.getItemCount(); i++) {
			final ITabPlug plug = plugs.getItem(i);
			Logger.check(plug.getContent() instanceof FragmentHolder,
					"Plug in PagerTabOutlet must contains a FragmentHolder content!");

			TabHost.TabSpec spec = tabHost.newTabSpec("" + i);
			spec.setIndicator(plug.getText() == null ? null : plug.getText()
					.getText(), plug.getIcon() == null ? null : plug.getIcon()
					.getAsDrawable());
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

		}
	}

	@Override
	public void onActivate(ITabPlug plug) {
		Logger.check(plug.getContent() instanceof ViewHolder, "The plug of FragmentPagerTabOutlet must include a ViewHolder!");
		TabHost tabHost = getAttachedObject().getTabHost();
		for(int i = 0; i < plugs.getItemCount(); i++){
			if (plugs.getItem(i) == plug){
				tabHost.setCurrentTab(i);
				break;				
			}
		}		
	}
}
