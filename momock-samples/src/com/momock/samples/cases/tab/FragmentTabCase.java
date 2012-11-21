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
import com.momock.holder.TextHolder;
import com.momock.holder.ViewHolder;
import com.momock.outlet.IOutlet;
import com.momock.outlet.action.ActionPlug;
import com.momock.outlet.action.IActionPlug;
import com.momock.outlet.card.CardPlug;
import com.momock.outlet.card.ICardPlug;
import com.momock.outlet.tab.FragmentTabOutlet;
import com.momock.outlet.tab.TabPlug;
import com.momock.samples.Outlets;
import com.momock.samples.R;

public class FragmentTabCase extends Case {

	public FragmentTabCase(ICase parent) {
		super(parent);
	}

	FragmentTabOutlet tabs = new FragmentTabOutlet();
	ICardPlug plug = CardPlug.get(FragmentHolder.get(R.layout.case_fragment_tab, this));
	@Override
	public void onCreate() {	
		IOutlet<IActionPlug, ViewHolder> outlet = getParent().getOutlet(Outlets.SAMPLES);
		outlet.addPlug(ActionPlug.get(new TextHolder("Fragment Tab Sample")).addExecuteEventHandler(new IEventHandler<IEventArgs>(){
			@Override
			public void process(Object sender, IEventArgs args) {
				run();
			}			
		}));

		tabs.addPlug(TabPlug.get(new TextHolder("Fragment Tab 1"), null, FragmentHolder.get(R.layout.tab_one)));
		tabs.addPlug(TabPlug.get(new TextHolder("Fragment Tab 2"), null, FragmentHolder.get(R.layout.tab_two)));
		tabs.addPlug(TabPlug.get(new TextHolder("Fragment Tab 3"), null, FragmentHolder.get(R.layout.tab_three)));
		tabs.addPlug(TabPlug.get(new TextHolder("Fragment Tab 4"), null, FragmentHolder.get(R.layout.tab_four)));
	}

	@Override
	public void run(Object... args) {
		getParent().getOutlet(Outlets.MAIN_CONTAINER).setActivePlug(plug);
	}

	@Override
	public void onAttach(Object target) {		
		tabs.attach(new FragmentTabHolder((Fragment)target, R.id.realtabcontent));
	}

}
