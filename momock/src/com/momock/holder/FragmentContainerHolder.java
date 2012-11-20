package com.momock.holder;

import java.lang.ref.WeakReference;

import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

public abstract class FragmentContainerHolder implements IComponentHolder{
	public abstract int getFragmentContainerId();
	public abstract FragmentManager getFragmentManager();
	public abstract FragmentActivity getAcivity();
	
	public static FragmentContainerHolder get(FragmentActivity activity, final int id)
	{
		final WeakReference<FragmentActivity> refActivity = new WeakReference<FragmentActivity>(activity);
		return new FragmentContainerHolder(){

			@Override
			public int getFragmentContainerId() {
				return id;
			}

			@Override
			public FragmentManager getFragmentManager() {
				return getAcivity() == null ? null : getAcivity().getSupportFragmentManager();
			}

			@Override
			public FragmentActivity getAcivity() {
				return refActivity == null ? null : refActivity.get();
			}			
		};
	}
}
