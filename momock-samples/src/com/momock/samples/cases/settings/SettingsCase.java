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
package com.momock.samples.cases.settings;

import android.app.Activity;

import com.momock.app.App;
import com.momock.app.Case;
import com.momock.event.EventArgs;
import com.momock.event.IEventHandler;
import com.momock.holder.ImageHolder;
import com.momock.holder.TextHolder;
import com.momock.outlet.action.ActionPlug;
import com.momock.samples.CaseNames;
import com.momock.samples.OutletNames;
import com.momock.samples.R;

public class SettingsCase extends Case<Activity> {

	public SettingsCase(String name) {
		super(name);
	}

	@Override
	public void onCreate() {
		ActionPlug self = ActionPlug.create(TextHolder.get("Settings"),
				ImageHolder.get(R.drawable.ic_action_google_play),
				new IEventHandler<EventArgs>() {
					@Override
					public void process(Object sender, EventArgs args) {
						run();
					}
				});
		getOutlet(OutletNames.MAIN_MENU).addPlug(self);
		App.get().getCase(CaseNames.MAIN).getOutlet(OutletNames.SAMPLES).addPlug(self);
	}

	@Override
	public void run(Object... args) {
		App.get().startActivity(SettingsActivity.class);
	}

}
