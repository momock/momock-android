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

import android.support.v4.app.Fragment;

import com.momock.app.Case;
import com.momock.app.ICase;
import com.momock.event.IEventArgs;
import com.momock.event.IEventHandler;
import com.momock.holder.FragmentHolder;
import com.momock.holder.FragmentTabHolder;
import com.momock.holder.ImageHolder;
import com.momock.holder.TextHolder;
import com.momock.holder.ViewHolder;
import com.momock.outlet.IOutlet;
import com.momock.outlet.action.ActionPlug;
import com.momock.outlet.action.IActionPlug;
import com.momock.outlet.card.CardPlug;
import com.momock.outlet.card.ICardPlug;
import com.momock.outlet.tab.FragmentPagerTabOutlet;
import com.momock.outlet.tab.TabPlug;
import com.momock.samples.OutletNames;
import com.momock.samples.R;

public class FragmentPagerTabCase extends Case<Fragment> {

	public FragmentPagerTabCase(ICase<?> parent) {
		super(parent);
	}

	FragmentPagerTabOutlet tabs = new FragmentPagerTabOutlet();

	@Override
	public void onCreate() {
		IOutlet<IActionPlug, ViewHolder> outlet = getOutlet(OutletNames.SAMPLES);
		outlet.addPlug(ActionPlug.create(
				TextHolder.get("Fragment Pager Tab Sample"))
				.addExecuteEventHandler(new IEventHandler<IEventArgs>() {
					@Override
					public void process(Object sender, IEventArgs args) {
						run();
					}
				}));

		tabs.addPlug(TabPlug.create(TextHolder.get("Pager Tab 1"),
				ImageHolder.get(R.drawable.ic_action_alarm_2),
				FragmentHolder.create(R.layout.tab_one)));
		tabs.addPlug(TabPlug.create(TextHolder.get("Pager Tab 2"),
				ImageHolder.get(R.drawable.ic_action_calculator),
				FragmentHolder.create(R.layout.tab_two)));
		tabs.addPlug(TabPlug.create(TextHolder.get("Pager Tab 3"),
				ImageHolder.get(R.drawable.ic_action_google_play),
				FragmentHolder.create(R.layout.tab_three)));
		tabs.addPlug(TabPlug.create(TextHolder.get("Pager Tab 4"),
				ImageHolder.get(R.drawable.ic_action_line_chart),
				FragmentHolder.create(R.layout.tab_four)));
	}

	ICardPlug self = CardPlug.create(FragmentHolder.create(R.layout.case_pager_tab,
			this));

	@Override
	public void run(Object... args) {
		getOutlet(OutletNames.MAIN_CONTAINER).setActivePlug(self);
	}

	@Override
	public void onAttach(final Fragment target) {
		tabs.attach(FragmentTabHolder.get(target, R.id.pager));
	}
}
