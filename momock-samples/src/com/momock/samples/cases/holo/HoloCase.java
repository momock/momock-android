package com.momock.samples.cases.holo;

import com.momock.app.App;
import com.momock.app.Case;
import com.momock.event.IEventArgs;
import com.momock.event.IEventHandler;
import com.momock.holder.TextHolder;
import com.momock.outlet.action.ActionPlug;
import com.momock.samples.Cases;
import com.momock.samples.Outlets;

public class HoloCase extends Case<HoloActionBarActivity>{

	public HoloCase(String name) {
		super(name);
	}

	@Override
	public void onCreate() {
		ActionPlug self = ActionPlug.get(TextHolder.get("I'm Holo"),
				new IEventHandler<IEventArgs>() {
					@Override
					public void process(Object sender, IEventArgs args) {
						run();
					}
				});
		App.get().getCase(Cases.MAIN).getOutlet(Outlets.SAMPLES).addPlug(self);
	}

	@Override
	public void run(Object... args) {
		App.get().startActivity(HoloActionBarActivity.class);
	}

}
