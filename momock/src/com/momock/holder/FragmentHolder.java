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

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.momock.app.CaseFragment;
import com.momock.app.ICase;
import com.momock.util.Logger;

public abstract class FragmentHolder implements IComponentHolder{
	public abstract Fragment getFragment();
	public static class SimpleFragment extends CaseFragment
	{
		public static final String RID = "RID";
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			int resourceId = getArguments().getInt(RID);
			return ViewHolder.get(resourceId).getView();
		}
	}
	public static FragmentHolder get(final Fragment f)
	{
		return new FragmentHolder(){

			@Override
			public Fragment getFragment() {
				return f;
			}
			
		};
	}
	public static <T extends Fragment> FragmentHolder get(final Class<T> fc)
	{
		return new FragmentHolder(){
			Fragment fragment = null;
			@Override
			public Fragment getFragment() {
				if (fragment == null){
					try {
						Logger.debug("Creating fragment " + fc.getName());
						fragment = fc.newInstance();
					} catch (Exception e) {
						Logger.error(e.getMessage());
					}
				}
				return fragment;
			}			
		};
	}
	public static FragmentHolder get(final int resourceId){
		return new FragmentHolder(){
			@Override
			public Fragment getFragment() {
				Bundle args = new Bundle();
				args.putInt(SimpleFragment.RID, resourceId);
				SimpleFragment f = new SimpleFragment();
				f.setArguments(args);
				return f;
			}			
		};
	}

	public static <T extends CaseFragment> FragmentHolder get(final Class<T> fc, final ICase kase)
	{
		return new FragmentHolder(){
			CaseFragment fragment = null;
			@Override
			public Fragment getFragment() {
				if (fragment == null){
					try {
						Logger.debug("Creating fragment " + fc.getName());
						fragment = fc.newInstance();
						fragment.setCase(kase);
					} catch (Exception e) {
						Logger.error(e.getMessage());
					}
				}
				return fragment;
			}			
		};
	}
	public static FragmentHolder get(final int resourceId, final ICase kase){
		return new FragmentHolder(){
			@Override
			public Fragment getFragment() {
				Bundle args = new Bundle();
				args.putInt(SimpleFragment.RID, resourceId);
				SimpleFragment f = new SimpleFragment();
				f.setArguments(args);
				f.setCase(kase);
				return f;
			}			
		};
	}
}
