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

import android.widget.ListView;

import com.momock.binder.container.ListViewBinder;
import com.momock.event.IEventHandler;
import com.momock.event.ItemEventArgs;
import com.momock.holder.ViewHolder;
import com.momock.outlet.Outlet;
import com.momock.util.Logger;

public class ListViewActionOutlet extends Outlet implements IActionOutlet{
	ListViewBinder binder;
	public ListViewActionOutlet(ListViewBinder binder){
		this.binder = binder;
		binder.getItemClickedEvent().addEventHandler(new IEventHandler<ItemEventArgs>() {

			@Override
			public void process(Object sender, ItemEventArgs args) {
				IActionPlug plug = (IActionPlug) args.getItem();
				plug.getExecuteEvent().fireEvent(plug, null);
			}

		});
	}	
	public void attach(ViewHolder target){
		Logger.check(target.getView() instanceof ListView, "Parameter type error!");
		attach((ListView)target.getView());
	}
	public void attach(ListView target){		
		binder.bind(target, getPlugs());
	}
}
