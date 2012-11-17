package com.momock.outlet.action;

import android.view.Menu;
import android.view.MenuItem;

import com.momock.event.IEvent;
import com.momock.event.IEventArgs;
import com.momock.outlet.Outlet;

public class MenuActionOutlet extends Outlet<IActionPlug>{
	public void attach(Menu menu)
	{
		if (plugs == null) return;
		for(IActionPlug plug : plugs)
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
					event.fireEvent(mi, null);
					return true;
				}
			});
		}
	}
}
