package com.momock.outlet.card;

import com.momock.holder.FragmentContainerHolder;
import com.momock.outlet.Outlet;

public class PagerCardOutlet extends Outlet<ICardPlug, FragmentContainerHolder>{
	@Override
	public void onAttach(FragmentContainerHolder target) {
		setActivePlug(getActivePlug());
	}
	
	@Override
	public void setActivePlug(ICardPlug plug) {
		activePlug = plug;
		if (getAttachedObject() != null)
		{
		
		}
	}
}
