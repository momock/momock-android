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

import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.momock.holder.FragmentHolder;
import com.momock.holder.FragmentManagerHolder;
import com.momock.holder.IComponentHolder;
import com.momock.outlet.IPlug;
import com.momock.outlet.Outlet;

public class FragmentCardOutlet extends Outlet implements ICardOutlet{	
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
		if (fmh != null && fmh.getFragmentManager() != null && plug != null)
		{
			new Handler().post(new Runnable(){

				@Override
				public void run() {
					IComponentHolder ch = ((ICardPlug)plug).getComponent();
					if (plug != null && ch instanceof FragmentHolder)
					{
						FragmentManager fm = fmh.getFragmentManager();
						FragmentTransaction ft = fm.beginTransaction();
						FragmentHolder fh = (FragmentHolder)ch;
						ft.replace(containerId, fh.getFragment());
						ft.commit();
						fm.executePendingTransactions();
					} 
				}
				
			});
		}
	}

}
