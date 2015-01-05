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

import android.app.Fragment;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.momock.app.Case;
import com.momock.app.ICase;
import com.momock.event.EventArgs;
import com.momock.event.IEventHandler;
import com.momock.holder.FragmentHolder;
import com.momock.holder.TextHolder;
import com.momock.holder.ViewHolder;
import com.momock.outlet.action.ActionPlug;
import com.momock.outlet.card.CardPlug;
import com.momock.outlet.card.ICardPlug;
import com.momock.samples.OutletNames;
import com.momock.samples.R;

public class ActionCase extends Case<Fragment>{
	public ActionCase(ICase<?> parent) {
		super(parent);
	}

	@Override
	public void onCreate() {	
		getOutlet(OutletNames.SAMPLES).addPlug(ActionPlug.create(TextHolder.get("Action Sample")).addExecuteEventHandler(new IEventHandler<EventArgs>(){
			@Override
			public void process(Object sender, EventArgs args) {
				run();
			}			
		}));
	}
	
	ICardPlug self = CardPlug.create(FragmentHolder.create(R.layout.case_action, this));

	@Override
	public void run(Object... args) {
		getOutlet(OutletNames.MAIN_CONTAINER).setActivePlug(self);
	}

	@Override
	public void onAttach(Fragment target) {
		View view = target.getView();
		Button btn = (Button)ViewHolder.get(view, R.id.button1).getView();
		btn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Toast.makeText(v.getContext(), "Hello", Toast.LENGTH_LONG).show();
			}
		});
	}

}
