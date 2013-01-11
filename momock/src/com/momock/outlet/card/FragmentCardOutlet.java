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
package com.momock.outlet.card;

import java.lang.ref.WeakReference;

import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.momock.holder.FragmentHolder;
import com.momock.holder.FragmentManagerHolder;
import com.momock.holder.IComponentHolder;
import com.momock.outlet.IPlug;
import com.momock.outlet.Outlet;

public class FragmentCardOutlet extends Outlet implements ICardOutlet{	
	WeakReference<Fragment> refLastFragment = null;
	FragmentManagerHolder fmh;
	int containerId;
	public FragmentCardOutlet(int containerId){
		this.containerId = containerId;		
	}
	public void attach(FragmentManagerHolder fmh) {
		this.fmh = fmh;
		setActivePlug(getActivePlug());
	}
	
	@Override
	public void setActivePlug(final IPlug plug) {
		activePlug = plug;
		if (fmh != null && plug != null)
		{
			new Handler().post(new Runnable(){

				@Override
				public void run() {
					FragmentManager fm = fmh.getFragmentManager();
					FragmentTransaction ft = fm.beginTransaction();
					Fragment lastFragment = refLastFragment == null || refLastFragment.get() == null ? null : refLastFragment.get();
					if (lastFragment != null) {
						ft.detach(lastFragment);
					}
					IComponentHolder ch = ((ICardPlug)plug).getComponent();
					if (plug != null && ch instanceof FragmentHolder)
					{
						FragmentHolder fh = (FragmentHolder)ch;
						if (!fh.isCreated())
							ft.add(containerId, fh.getFragment());
						else 
							ft.attach(fh.getFragment());	
						refLastFragment = new WeakReference<Fragment>(fh.getFragment());
					} else {
						refLastFragment = null;
					}
					ft.commit();
					fm.executePendingTransactions();
				}
				
			});
		}
	}

}
