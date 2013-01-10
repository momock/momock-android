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

import android.view.View;
import android.widget.TabHost;
import android.widget.TabHost.TabContentFactory;

import com.momock.data.IDataList;
import com.momock.holder.TabHolder;
import com.momock.holder.ViewHolder;
import com.momock.outlet.IPlug;
import com.momock.outlet.Outlet;
import com.momock.util.Logger;

public class TabOutlet extends Outlet implements ITabOutlet{
	IDataList<IPlug> plugs;
	TabHolder target;
	public void attach(TabHolder target) {
		Logger.check(target != null, "Parameter target cannot be null!");
		this.target = target;
		final TabHost tabHost = target.getTabHost();
		tabHost.setup();
		plugs = getPlugs();
		for(int i = 0; i < plugs.getItemCount(); i++)
		{
			final ITabPlug plug = (ITabPlug)plugs.getItem(i);
			Logger.check(plug.getContent() instanceof ViewHolder, "TabOutlet could only contains ViewHolder content");
			((ViewHolder)plug.getContent()).reset();
	        TabHost.TabSpec spec = tabHost.newTabSpec("");
	        target.setTabIndicator(spec, plug);
	        spec.setContent(new TabContentFactory(){

				@Override
				public View createTabContent(String tag) {
					View view = ((ViewHolder)plug.getContent()).getView();
					return view;
				}
	        	
	        });
	        tabHost.addTab(spec);	
	        if (getActivePlug() == plug)
	        	tabHost.setCurrentTab(i);
		}
		tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {

			@Override
			public void onTabChanged(String tabId) {
				int index = tabHost.getCurrentTab();
				setActivePlug(plugs.getItem(index));
			}
		});
	}
	@Override
	public void onActivate(IPlug plug) {
		if (((ITabPlug)plug).getContent() != null){
			TabHost tabHost = target.getTabHost();
			tabHost.setCurrentTab(getIndexOf(plug));
		} else {
			Logger.debug("The plug of TabOutlet has not been attached !");			
		}
	}
}
