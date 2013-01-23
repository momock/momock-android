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
import android.view.View;
import android.widget.TabHost;
import android.widget.TabHost.TabContentFactory;

import com.momock.data.IDataList;
import com.momock.holder.FragmentHolder;
import com.momock.holder.FragmentTabHolder;
import com.momock.outlet.IPlug;
import com.momock.outlet.Outlet;
import com.momock.util.Logger;

public class FragmentTabOutlet extends Outlet implements ITabOutlet {
	FragmentTabHolder target;
	public void attach(FragmentTabHolder tabHolder) {
		Logger.check(tabHolder != null, "Parameter tabHolder cannot be null!");
		this.target = tabHolder;
		TabHost tabHost = target.getTabHost();
		final IDataList<IPlug> plugs = getPlugs();
		tabHost.setup();
		tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
			
			@Override
			public void onTabChanged(String tabId) {
				new Handler().post(new Runnable(){

					@Override
					public void run() {
						int index = target.getTabHost().getCurrentTab();
						int id = target.getTabContentId();
						ITabPlug plug = (ITabPlug)plugs.getItem(index);
						setActivePlug(plug);

						FragmentManager fm = target.getFragmentManager();
						if (fm != null && plug.getContent() instanceof FragmentHolder)
						{
							try{
								FragmentTransaction ft = fm.beginTransaction();
								FragmentHolder fh = (FragmentHolder)plug.getContent();
								ft.replace(id, fh.getFragment());
								ft.commit();
								fm.executePendingTransactions();
							}catch(Exception e){
								Logger.error(e);
							}
						}
					}
					
				});
			}
		});
		for(int i = 0; i < plugs.getItemCount(); i++)
		{
			final ITabPlug plug = (ITabPlug)plugs.getItem(i);
			if (plug.getContent() instanceof FragmentHolder)
			{
		        TabHost.TabSpec spec = tabHost.newTabSpec("" + i);
		        target.setTabIndicator(spec, plug);
		        spec.setContent(new TabContentFactory(){

					@Override
					public View createTabContent(String tag) {
		                View v = new View(target.getTabHost().getContext());
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
	}

	@Override
	public void onActivate(IPlug plug) {
		if (((ITabPlug)plug).getContent() != null && target != null){
			TabHost tabHost = target.getTabHost();
			tabHost.setCurrentTab(getIndexOf(plug));
		} 
	}
}
