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
package com.momock.holder;

import java.lang.ref.WeakReference;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

import com.momock.app.ICase;

public abstract class FragmentManagerHolder{
	public abstract FragmentManager getFragmentManager();
	
	public static FragmentManagerHolder get(FragmentActivity activity)
	{
		final WeakReference<FragmentActivity> refActivity = new WeakReference<FragmentActivity>(activity);
		return new FragmentManagerHolder(){
			
			@Override
			public FragmentManager getFragmentManager() {
				return refActivity.get().getSupportFragmentManager();
			}
	
		};
	}
	public static FragmentManagerHolder get(final Fragment fragment)
	{
		return new FragmentManagerHolder(){


			@Override
			public FragmentManager getFragmentManager() {
				return fragment.getFragmentManager();
			}
	
		};
	}

	public static FragmentManagerHolder get(final ICase<?> kase)
	{
		return new FragmentManagerHolder(){

			@Override
			public FragmentManager getFragmentManager() {
				if (kase.getAttachedObject() instanceof FragmentActivity)
					return ((FragmentActivity)kase.getAttachedObject()).getSupportFragmentManager();
				else if (kase.getAttachedObject() instanceof Fragment)
					return ((Fragment)kase.getAttachedObject()).getFragmentManager();
				return null;
			}
	
		};
	}
}
