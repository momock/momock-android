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
package com.momock.samples.cases.tab;

import javax.inject.Inject;

import android.app.Fragment;

import com.momock.app.Case;
import com.momock.app.ICase;
import com.momock.event.EventArgs;
import com.momock.event.IEventHandler;
import com.momock.holder.FragmentHolder;
import com.momock.holder.TabHolder;
import com.momock.holder.TextHolder;
import com.momock.outlet.IOutlet;
import com.momock.outlet.action.ActionPlug;
import com.momock.outlet.card.CardPlug;
import com.momock.outlet.card.ICardPlug;
import com.momock.outlet.tab.PagerTabOutlet;
import com.momock.samples.OutletNames;
import com.momock.samples.R;
import com.momock.samples.services.IDataService;

public class DynamicPagerTabCase extends Case<Fragment> {
	@Inject
	IDataService ds;
	public DynamicPagerTabCase(ICase<?> parent) {
		super(parent);
	}

	PagerTabOutlet tabs = new PagerTabOutlet();

	@Override
	public void onCreate() {
		IOutlet outlet = getOutlet(OutletNames.SAMPLES);
		outlet.addPlug(ActionPlug.create(TextHolder.get("Dynamic Pager Tab Sample"))
				.addExecuteEventHandler(new IEventHandler<EventArgs>() {
					@Override
					public void process(Object sender, EventArgs args) {
						run();
					}
				}));

		tabs.setPlugProvider(new CategoryPlugProvider(this, ds.getAllCategories()));
	}

	ICardPlug self = CardPlug.create(FragmentHolder.create(R.layout.case_pager_tab,
			this));

	@Override
	public void run(Object... args) {
		getOutlet(OutletNames.MAIN_CONTAINER).setActivePlug(self);
	}

	@Override
	public void onAttach(final Fragment target) {
		tabs.attach(TabHolder.get(target, R.id.pager));
	}
}
