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

import com.momock.binder.AdapterViewBinder;
import com.momock.binder.AdapterViewBinder.ItemEventArgs;
import com.momock.binder.ListViewBinder;
import com.momock.event.IEventHandler;
import com.momock.holder.ViewHolder;
import com.momock.outlet.Outlet;

public class ListViewActionOutlet extends Outlet<IActionPlug, ViewHolder> implements IActionOutlet<ViewHolder>{

	public static ListViewActionOutlet getSimple() {
		return new ListViewActionOutlet() {
			@Override
			public void onAttach(ViewHolder target) {
				ListViewBinder binder = ListViewBinder.getSimple("Text");
				binder.addItemClickedEventHandler(new IEventHandler<AdapterViewBinder.ItemEventArgs>() {

					@Override
					public void process(Object sender, ItemEventArgs args) {
						IActionPlug plug = (IActionPlug) args.getItem();
						plug.getExecuteEvent().fireEvent(plug, null);
					}

				});
				binder.bind(target, getPlugs());
			}
		};
	}
}
