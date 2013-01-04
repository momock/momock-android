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
package com.momock.samples.cases.card;

import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.Button;

import com.momock.app.App;
import com.momock.app.Case;
import com.momock.app.ICase;
import com.momock.data.IDataList;
import com.momock.event.EventArgs;
import com.momock.event.IEventHandler;
import com.momock.holder.FragmentHolder;
import com.momock.holder.TextHolder;
import com.momock.holder.ViewHolder;
import com.momock.outlet.IOutlet;
import com.momock.outlet.action.ActionPlug;
import com.momock.outlet.card.CardPlug;
import com.momock.outlet.card.ICardPlug;
import com.momock.outlet.card.PagerCardOutlet;
import com.momock.samples.OutletNames;
import com.momock.samples.R;

public class PagerCardCase extends Case<Fragment> implements View.OnClickListener {

	public PagerCardCase(ICase<?> parent) {
		super(parent);
	}

	PagerCardOutlet cards = new PagerCardOutlet();

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void onCreate() {
		IOutlet outlet = getParent().getOutlet(OutletNames.SAMPLES);
		outlet.addPlug(ActionPlug.create(TextHolder.get("Pager Card Sample"))
				.addExecuteEventHandler(new IEventHandler<EventArgs>() {
					@Override
					public void process(Object sender, EventArgs args) {
						run();
					}
				}));

		cards.addPlug(CardPlug.create(ViewHolder.create(App.get(), R.layout.tab_one)));
		cards.addPlug(CardPlug.create(ViewHolder.create(App.get(), R.layout.tab_two)));
		cards.addPlug(CardPlug.create(ViewHolder.create(App.get(), R.layout.tab_three)));
	}

	@Override
	public void onAttach(Fragment target) {
		cards.attach(ViewHolder.get(target, R.id.pager));
		Button btn1 = (Button) ViewHolder.get(target, R.id.button1).getView();		
		Button btn2 = (Button) ViewHolder.get(target, R.id.button2).getView();
		Button btn3 = (Button) ViewHolder.get(target, R.id.button3).getView();
		btn1.setOnClickListener(this);
		btn2.setOnClickListener(this);
		btn3.setOnClickListener(this);
	}

	ICardPlug self = CardPlug.create(FragmentHolder.create(R.layout.case_pager_card, this));

	@Override
	public void run(Object... args) {
		getOutlet(OutletNames.MAIN_CONTAINER).setActivePlug(self);
	}

	@Override
	public void onClick(View v) {
		IDataList<ICardPlug> plugs = cards.getPlugs();
		switch (v.getId()) {
		case R.id.button1:
			cards.setActivePlug(plugs.getItem(0));
			break;
		case R.id.button2:
			cards.setActivePlug(plugs.getItem(1));
			break;
		case R.id.button3:
			cards.setActivePlug(plugs.getItem(2));
			break;
		}

	}

}
