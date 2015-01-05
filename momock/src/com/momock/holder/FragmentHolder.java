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

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.momock.app.CaseFragment;
import com.momock.app.ICase;
import com.momock.util.Logger;

public abstract class FragmentHolder implements IComponentHolder{
	public abstract Fragment getFragment();
	public static class SimpleFragment extends Fragment{
		public static final String RID = "RID";
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			int resourceId = getArguments().getInt(RID);
			Logger.debug("Create SimpleFragment RID=" + resourceId);
			return ViewHolder.create(container.getContext(), resourceId).getView();
		}
	}
	public static class SimpleCaseFragment extends CaseFragment
	{
		public static final String RID = "RID";
		public static final String CASE_ID = "CASE_ID";
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			int resourceId = getArguments().getInt(RID);
			String name = getArguments().getString(CASE_ID);
			Logger.check(name != null, "Parameter error!");
			Logger.debug("Create SimpleFragment RID=" + resourceId + " CASE ID=" + name);
			try{				
				return ViewHolder.create(inflater.getContext(), resourceId).getView();
			}catch(Exception e){
				Logger.error(e);
				return null;
			}
		}
		@Override
		protected String getCaseName() {
			String name = getArguments().getString(CASE_ID);
			return name;
		}
	}
	public static <T extends Fragment> FragmentHolder create(final Class<T> fc, final ICase<Fragment> kase)
	{
		return new FragmentHolder(){
			@Override
			public Fragment getFragment() {
				try {
					Logger.debug("Creating fragment " + fc.getName());
					CaseFragment fragment = (CaseFragment)fc.newInstance();
					fragment.setCase(kase);
					return fragment;
				} catch (Exception e) {
					Logger.error(e);
					return null;
				}
			}	
		};
	}
	public static FragmentHolder create(final int resourceId){
		return new FragmentHolder(){
			@Override
			public Fragment getFragment() {
				Bundle args = new Bundle();
				args.putInt(SimpleFragment.RID, resourceId);
				Fragment fragment = new SimpleFragment();
				fragment.setArguments(args);
				return fragment;
			}			
		};
	}

	public static FragmentHolder create(final int resourceId, final ICase<Fragment> kase){
		return new FragmentHolder(){
			@Override
			public Fragment getFragment() {
				Bundle args = new Bundle();
				args.putInt(SimpleCaseFragment.RID, resourceId);
				args.putString(SimpleCaseFragment.CASE_ID, kase.getFullName());
				Fragment fragment = new SimpleCaseFragment();
				fragment.setArguments(args);
				return fragment;
			}			
		};
	}
}
