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
import android.view.View;
import android.widget.TabHost;
import android.widget.TabHost.TabContentFactory;

import com.momock.data.IDataList;
import com.momock.holder.FragmentHolder;
import com.momock.holder.FragmentTabHolder;
import com.momock.holder.ViewHolder;
import com.momock.outlet.Outlet;
import com.momock.util.Logger;

public class FragmentTabOutlet extends Outlet<ITabPlug, FragmentTabHolder> implements ITabOutlet<FragmentTabHolder> {
	Fragment lastFragment = null;	
	@Override
	public void onAttach(final FragmentTabHolder target) {
		Logger.check(target != null, "Parameter target cannot be null!");
		final TabHost tabHost = target.getTabHost();
		final IDataList<ITabPlug> plugs = getPlugs();
		tabHost.setup();
		tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
			
			@Override
			public void onTabChanged(String tabId) {
				int index = tabHost.getCurrentTab();
				int id = target.getTabContentId();
				ITabPlug plug = plugs.getItem(index);
				FragmentManager fm = getAttachedObject().getFragmentManager();
				FragmentTransaction ft = fm.beginTransaction();

				if (lastFragment != null) {
					ft.detach(lastFragment);
				}

				if (plug.getContent() instanceof FragmentHolder)
				{
					FragmentHolder fh = (FragmentHolder)plug.getContent();
					if (!fh.isCreated())
						ft.add(id, fh.getFragment());
					else 
						ft.attach(fh.getFragment());	
					lastFragment = fh.getFragment();
				} else {
					lastFragment = null;
				}
				ft.commit();
				fm.executePendingTransactions();
			}
		});
		for(int i = 0; i < plugs.getItemCount(); i++)
		{
			final ITabPlug plug = plugs.getItem(i);
			if (plug.getContent() instanceof FragmentHolder)
			{
		        TabHost.TabSpec spec = tabHost.newTabSpec("" + i);
		        spec.setIndicator(plug.getText() == null ? null : plug.getText().getText(),
		        		plug.getIcon() == null ? null : plug.getIcon().getAsDrawable());        
		        spec.setContent(new TabContentFactory(){

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
	}

	@Override
	public void onActivate(ITabPlug plug) {
		if (plug.getContent() != null){
			TabHost tabHost = getAttachedObject().getTabHost();
			tabHost.setCurrentTab(getIndexOf(plug));
		} else {
			Logger.debug("The plug of FragmentTabOutlet has not been attached !");			
		}
	}
}
