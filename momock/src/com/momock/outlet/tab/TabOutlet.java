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

import com.momock.holder.TabHolder;
import com.momock.holder.ViewHolder;
import com.momock.outlet.Outlet;

public class TabOutlet extends Outlet<ITabPlug> {

	@Override
	public void onAttach(Object target) {
		TabHolder tab = (TabHolder)target;
		if (tab == null)
		{
			throw new RuntimeException("TabOutlet must attach with a TabHolder!");
		}
		tab.getTabHost().setup();
		for(final ITabPlug plug : plugs)
		{
			if (plug.getContent() instanceof ViewHolder)
			{
		        TabHost.TabSpec spec = tab.getTabHost().newTabSpec("");
		        spec.setIndicator(plug.getText() == null ? null : plug.getText().getText(),
		        		plug.getIcon() == null ? null : plug.getIcon().getAsDrawable());        
		        spec.setContent(new TabContentFactory(){

					@Override
					public View createTabContent(String tag) {
						return ((ViewHolder)plug.getContent()).getView();
					}
		        	
		        });
		        tab.getTabHost().addTab(spec);		
			}	
		}
	}
	
}
