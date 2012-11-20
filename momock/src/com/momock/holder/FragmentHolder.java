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

import com.momock.util.Logger;

public abstract class FragmentHolder implements IComponentHolder{
	public abstract Fragment getFragment();

	public static FragmentHolder get(final Fragment f)
	{
		return new FragmentHolder(){

			@Override
			public Fragment getFragment() {
				return f;
			}
			
		};
	}
	public static FragmentHolder get(final Class<?> fc)
	{
		return new FragmentHolder(){
			Fragment fragment = null;
			@Override
			public Fragment getFragment() {
				if (fragment == null){
					try {
						Logger.debug("Creating fragment " + fc.getName());
						fragment = (Fragment)fc.newInstance();
					} catch (Exception e) {
						Logger.error(e.getMessage());
					}
				}
				return fragment;
			}			
		};
	}
}
