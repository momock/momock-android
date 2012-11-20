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
package com.momock.samples.cases.action;

import com.momock.app.App;
import com.momock.app.Case;
import com.momock.holder.TextHolder;
import com.momock.outlet.IOutlet;
import com.momock.outlet.action.ActionPlug;
import com.momock.outlet.action.IActionPlug;
import com.momock.samples.Outlets;

public class ActionCase extends Case{

	@Override
	protected void onCreate() {
		IOutlet<IActionPlug> outlet = App.get().getOutlet(Outlets.SAMPLES);
		outlet.addPlug(ActionPlug.get(TextHolder.get("Action Sample")));
	}

}
