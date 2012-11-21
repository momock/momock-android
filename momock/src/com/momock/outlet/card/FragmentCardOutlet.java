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

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.momock.holder.FragmentContainerHolder;
import com.momock.holder.FragmentHolder;
import com.momock.outlet.Outlet;

public class FragmentCardOutlet extends Outlet<ICardPlug, FragmentContainerHolder> {	
	@Override
	public void onAttach(FragmentContainerHolder target) {
		setActivePlug(getActivePlug());
	}
	
	@Override
	public void setActivePlug(ICardPlug plug) {
		activePlug = plug;
		if (getAttachedObject() != null)
		{
			int id = getAttachedObject().getFragmentContainerId();
			FragmentManager fm = getAttachedObject().getFragmentManager();
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
			//fm.executePendingTransactions();
		}
	}

}
