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
import android.view.View;

import com.momock.app.Case;
import com.momock.app.ICase;
import com.momock.holder.FragmentHolder;
import com.momock.holder.ViewHolder;
import com.momock.outlet.IOutlet;
import com.momock.outlet.action.ListViewActionOutlet;
import com.momock.outlet.card.CardPlug;
import com.momock.outlet.card.ICardPlug;
import com.momock.samples.Outlets;
import com.momock.samples.R;

public class MainMenuCase extends Case {
	ICardPlug plug;
	public MainMenuCase(ICase parent) {
		super(parent);
	}

	@Override
	protected void onCreate() {
		getParent().addOutlet(Outlets.SAMPLES, ListViewActionOutlet.getSimple());
		plug = CardPlug.get(FragmentHolder.get(R.layout.fragment_mainmenu, this));
		run();
	}

	@Override
	public void run() {
		IOutlet<ICardPlug> outlet = getParent().getOutlet(Outlets.MAIN_CONTAINER);
		outlet.setActivePlug(plug);
	}

	@Override
	public void onAttach(Object target) {
		View view = ((Fragment)target).getView();
        getOutlet(Outlets.SAMPLES).attach(ViewHolder.get(view, R.id.lvMainMenu));      

	}

}
