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
package com.momock.samples;

import com.momock.app.App;
import com.momock.app.Case;
import com.momock.outlet.action.MenuActionOutlet;
import com.momock.outlet.card.FragmentCardOutlet;
import com.momock.samples.cases.action.ActionCase;
import com.momock.samples.cases.mainmenu.MainMenuCase;
import com.momock.samples.cases.tab.FragmentTabCase;
import com.momock.samples.cases.tab.TabCase;

public class MainCase extends Case{

	public MainCase(String name) {
		super(name);
	}

	@Override
	public void onCreate() {		
		App.get().addOutlet(Outlets.MAIN_MENU, new MenuActionOutlet());
			
		addOutlet(Outlets.MAIN_CONTAINER, new FragmentCardOutlet());
		
		addCase(new MainMenuCase(this));
		addCase(new ActionCase(this));
		addCase(new TabCase(this));
		addCase(new FragmentTabCase(this));
	}

}
