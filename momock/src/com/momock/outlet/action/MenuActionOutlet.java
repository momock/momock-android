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
package com.momock.outlet.action;

import android.view.Menu;
import android.view.MenuItem;

import com.momock.event.IEvent;
import com.momock.event.IEventArgs;
import com.momock.outlet.Outlet;

public class MenuActionOutlet extends Outlet<IActionPlug>{
	public void onAttach(Object target)
	{
		if (plugs == null || !(target instanceof Menu)) return;		
		Menu menu = (Menu)target;
		for(final IActionPlug plug : plugs)
		{
			String text = plug.getText() == null ? null : plug.getText().getText();
			final MenuItem mi = menu.add(text == null ? "" : text);
			if (plug.getIcon() != null)
			{
				mi.setIcon(plug.getIcon().getAsDrawable());
			}
			final IEvent<IEventArgs> event = plug.getExecuteEvent();
			mi.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
				
				@Override
				public boolean onMenuItemClick(MenuItem item) {
					event.fireEvent(plug, null);
					return true;
				}
			});
		}
	}
}
