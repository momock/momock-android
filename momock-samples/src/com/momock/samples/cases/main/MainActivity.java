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
package com.momock.samples.cases.main;

import android.os.Bundle;
import android.view.Menu;

import com.momock.app.App;
import com.momock.app.CaseActivity;
import com.momock.holder.FragmentContainerHolder;
import com.momock.outlet.action.IActionOutlet;
import com.momock.samples.CaseNames;
import com.momock.samples.OutletNames;
import com.momock.samples.PlugNames;
import com.momock.samples.R;
import com.momock.samples.R.id;
import com.momock.samples.R.layout;
import com.momock.samples.cases.mainmenu.MainMenuCase;

public class MainActivity extends CaseActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.case_main);
        
        getCase().getOutlet(OutletNames.MAIN_CONTAINER).attach(FragmentContainerHolder.get(this, R.id.fragment_content));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	IActionOutlet<Menu> outlet = getCase().getOutlet(OutletNames.MAIN_MENU);
    	outlet.attach(menu);
        return true;
    }

	@Override
	protected String getCaseName() {
		return CaseNames.MAIN;
	}

	@Override
	public void onBackPressed() {
		if (getCase().getOutlet(OutletNames.MAIN_CONTAINER).getActivePlug() == App.get().getPlug(PlugNames.MAIN_MENU))
			super.onBackPressed();
		else
			getCase().getCase(MainMenuCase.class.getName()).run();
	}
}
