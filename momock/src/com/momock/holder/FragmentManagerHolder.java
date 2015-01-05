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

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;

import com.momock.app.ICase;

public abstract class FragmentManagerHolder{
	public abstract FragmentManager getFragmentManager();
	
	public static FragmentManagerHolder get(Activity activity)
	{
		final WeakReference<Activity> refActivity = new WeakReference<Activity>(activity);
		return new FragmentManagerHolder(){
			
			@Override
			public FragmentManager getFragmentManager() {
				return refActivity.get().getFragmentManager();
			}
	
		};
	}
	public static FragmentManagerHolder get(final Fragment fragment)
	{
		return new FragmentManagerHolder(){


			@Override
			public FragmentManager getFragmentManager() {
				FragmentManager fm = fragment.getFragmentManager();
				if (fm == null && fragment.getActivity() != null)
					fm = fragment.getActivity().getFragmentManager();
				return fm;
			}
	
		};
	}

	public static FragmentManagerHolder get(final ICase<?> kase)
	{
		return new FragmentManagerHolder(){

			@Override
			public FragmentManager getFragmentManager() {
				FragmentManager fm = null;
				ICase<?> current = kase;
				while(current != null && fm == null){
					if (current.getAttachedObject() instanceof Activity)
						fm = ((Activity)current.getAttachedObject()).getFragmentManager();
					else if (current.getAttachedObject() instanceof Fragment){
						Fragment fragment = (Fragment)current.getAttachedObject();
						fm = fragment.getFragmentManager();
						if (fm == null && fragment.getActivity() != null)
							fm = fragment.getActivity().getFragmentManager();
					}
					current = current.getParent();
				}
				return fm;
			}
	
		};
	}
}
