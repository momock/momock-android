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

import android.annotation.TargetApi;
import android.os.Build;
import android.view.Menu;
import android.view.MenuItem;

import com.momock.data.IDataList;
import com.momock.event.EventArgs;
import com.momock.event.IEvent;
import com.momock.outlet.IPlug;
import com.momock.outlet.Outlet;

public class MenuActionOutlet extends Outlet implements IActionOutlet{
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	void enableShowAsAction(final MenuItem mi){
		mi.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);		
	}
	public void attach(Menu menu)
	{
		IDataList<IPlug> plugs = getPlugs();
		for(int i = 0; i < plugs.getItemCount(); i++)
		{
			final IActionPlug plug = (IActionPlug)plugs.getItem(i);
			String text = plug.getText() == null ? null : plug.getText().getText();
			final MenuItem mi = menu.add(text == null ? "" : text);
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
				enableShowAsAction(mi);
			if (plug.getIcon() != null)
			{
				mi.setIcon(plug.getIcon().getAsDrawable());
			}
			final IEvent<EventArgs> event = plug.getExecuteEvent();
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
