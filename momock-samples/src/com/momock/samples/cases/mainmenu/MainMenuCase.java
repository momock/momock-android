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
package com.momock.samples.cases.mainmenu;

import android.support.v4.app.Fragment;

import com.momock.app.App;
import com.momock.app.Case;
import com.momock.app.ICase;
import com.momock.holder.FragmentHolder;
import com.momock.holder.ViewHolder;
import com.momock.outlet.action.ListViewActionOutlet;
import com.momock.outlet.card.CardPlug;
import com.momock.outlet.card.ICardPlug;
import com.momock.samples.NamedPlugs;
import com.momock.samples.Outlets;
import com.momock.samples.R;

public class MainMenuCase extends Case<Fragment> {
	ICardPlug plug;
	public MainMenuCase(ICase<?> parent) {
		super(parent);
	}

	@Override
	public void onCreate() {
		getParent().addOutlet(Outlets.SAMPLES, ListViewActionOutlet.getSimple());
		plug = CardPlug.get(FragmentHolder.get(R.layout.case_mainmenu, this));
		App.get().addNamedPlug(NamedPlugs.MAIN_MENU, plug);
		run();
	}

	@Override
	public void run(Object... args) {
		getOutlet(Outlets.MAIN_CONTAINER).setActivePlug(plug);
	}

	@Override
	public void onAttach(Fragment target) {
        getOutlet(Outlets.SAMPLES).attach(ViewHolder.get(target, R.id.lvMainMenu));      

	}

}
