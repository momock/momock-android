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
package com.momock.samples.cases.binder;


import android.app.Fragment;

import com.momock.app.Case;
import com.momock.app.ICase;
import com.momock.binder.container.ListViewBinder;
import com.momock.event.EventArgs;
import com.momock.event.IEventHandler;
import com.momock.holder.FragmentHolder;
import com.momock.holder.TextHolder;
import com.momock.holder.ViewHolder;
import com.momock.outlet.IOutlet;
import com.momock.outlet.action.ActionPlug;
import com.momock.outlet.card.CardPlug;
import com.momock.outlet.card.ICardPlug;
import com.momock.samples.OutletNames;
import com.momock.samples.R;
import com.momock.samples.services.IDataService;

public class ListViewCase extends Case<Fragment>{

	public ListViewCase(ICase<?> parent) {
		super(parent);
	}
	
	@Override
	public void onCreate() {
		IOutlet outlet = getParent().getOutlet(OutletNames.SAMPLES);
		outlet.addPlug(ActionPlug.create(TextHolder.get("ListView Sample"))
				.addExecuteEventHandler(new IEventHandler<EventArgs>() {
					@Override
					public void process(Object sender, EventArgs args) {
						run();
					}
				}));
	}

	ICardPlug self = CardPlug.create(FragmentHolder.create(R.layout.case_listview, this));

	@Override
	public void run(Object... args) {
		getOutlet(OutletNames.MAIN_CONTAINER).setActivePlug(self);
	}	

	@Override
	public void onAttach(Fragment target) {
		IDataService ds = getService(IDataService.class);
		ListViewBinder binder = ListViewBinder.getSimple("Name");
		binder.bind(ViewHolder.get(target, R.id.lvproducts), ds.getAllProducts());
	}
}
