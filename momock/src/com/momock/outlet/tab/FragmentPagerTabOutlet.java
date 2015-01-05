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

import android.app.Fragment;
import android.os.Handler;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;
import android.widget.TabHost.TabContentFactory;
import android.widget.TabWidget;

import com.momock.app.CaseFragment;
import com.momock.app.IActiveCaseIndicator;
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
		tabContent.setAdapter(new FragmentPagerAdapter(target.getFragmentManager()) {

			@Override
			public Fragment getItem(final int position) {
				final ITabPlug plug = (ITabPlug)plugs.getItem(position);
				Fragment f = ((FragmentHolder)plug.getContent()).getFragment();
				if (f instanceof CaseFragment){
					CaseFragment cf = (CaseFragment)f;
					cf.setActiveCaseIndicator(new IActiveCaseIndicator(){

						@Override
						public boolean isActiveCase() {
							return getActivePlug() == plug;
						}
						
					});
				}
				return f;
			}

			@Override
			public int getCount() {
				return plugs.getItemCount();
			}
			void doFinishUpdate(ViewGroup container){
				super.finishUpdate(container);
			}
		    @Override
		    public void finishUpdate(final ViewGroup container) {
		    	new Handler().post(new Runnable(){

					@Override
					public void run() {
						doFinishUpdate(container);
					}
		    		
		    	});
		    }

			@Override
			public void setPrimaryItem(ViewGroup container, int position, final Object object) {
				super.setPrimaryItem(container, position, object);
				new Handler().post(new Runnable(){

					@Override
					public void run() {
						
						if (object instanceof CaseFragment){
							CaseFragment cf = (CaseFragment)object;
							if (cf.getCase() != null && cf.getCase().getParent() != null)
								cf.getCase().getParent().setActiveCase(cf.getCase());
						}
					}
					
				});			
			}
		});
		tabHost.setup();
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
			if (getActivePlug() == plug){
				tabHost.setCurrentTab(i);
				tabContent.setCurrentItem(i, true);
			}
		}
		tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {

			@Override
			public void onTabChanged(String tabId) {
				int index = tabHost.getCurrentTab();
				ITabPlug plug = (ITabPlug)plugs.getItem(index);
				setActivePlug(plug);
				tabContent.setCurrentItem(index, true);
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
	}

	@Override
	public void onActivate(IPlug plug) {
		if (((ITabPlug)plug).getContent() != null && target != null) {
			TabHost tabHost = target.getTabHost();
			tabHost.setCurrentTab(getIndexOf(plug));
		} 
	}
}
