package com.momock.samples.cases.database;

import android.support.v4.app.Fragment;

import com.momock.app.Case;
import com.momock.app.ICase;
import com.momock.event.EventArgs;
import com.momock.event.IEventHandler;
import com.momock.holder.FragmentHolder;
import com.momock.holder.TabHolder;
import com.momock.holder.TextHolder;
import com.momock.outlet.IOutlet;
import com.momock.outlet.action.ActionPlug;
import com.momock.outlet.card.CardPlug;
import com.momock.outlet.card.ICardPlug;
import com.momock.samples.OutletNames;
import com.momock.samples.R;

public class DatabaseCase extends Case<Fragment> {

	public DatabaseCase(ICase<?> parent) {
		super(parent);
	}

	ICardPlug self = CardPlug.create(FragmentHolder.create(R.layout.case_tab,
			this));

	@Override
	public void run(Object... args) {
		getOutlet(OutletNames.MAIN_CONTAINER).setActivePlug(self);
	}

	@Override
	public void onAttach(Fragment target) {
	}

	@Override
	public void onCreate() {
		IOutlet outlet = getParent().getOutlet(OutletNames.SAMPLES);
		outlet.addPlug(ActionPlug.create(TextHolder.get("Database Sample"))
				.addExecuteEventHandler(new IEventHandler<EventArgs>() {
					@Override
					public void process(Object sender, EventArgs args) {
						run();
					}
				}));

	}

}
