package com.momock.outlet.card;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.momock.holder.FragmentContainerHolder;
import com.momock.holder.FragmentHolder;
import com.momock.outlet.Outlet;

public class FragmentCardOutlet extends Outlet<ICardPlug>{
	FragmentContainerHolder getContainer()
	{
		return (FragmentContainerHolder)getAttachedObject();
	}
	@Override
	public void onAttach(Object target) {
		setActivePlug(getActivePlug());
	}
	
	@Override
	public void setActivePlug(ICardPlug plug) {
		activePlug = plug;
		if (getContainer() != null)
		{
			int id = getContainer().getFragmentContainerId();
			FragmentManager fm = getContainer().getFragmentManager();
			Fragment fragment = fm.findFragmentById(id);
			FragmentTransaction ft = fm.beginTransaction();
			if (plug != null && plug.getComponent() instanceof FragmentHolder)
			{
				FragmentHolder fh = (FragmentHolder)plug.getComponent();
				if (fragment == null)
					ft.add(id, fh.getFragment());
				else 
					ft.replace(id, fh.getFragment());							
				ft.commit();
			} else {
				if (fragment != null) {
					ft.remove(fragment);
					ft.commit();
				}
			}
		}
	}

}
