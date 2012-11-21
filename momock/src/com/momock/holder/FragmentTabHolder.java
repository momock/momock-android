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
import android.view.View;

public class FragmentTabHolder extends TabHolder {

	int tabContentId;
	WeakReference<FragmentManager> refFragmentManager;
	public int getTabContentId()	{
		return tabContentId;
	}
	public FragmentManager getFragmentManager(){
		return refFragmentManager.get();
	}

	public FragmentTabHolder(FragmentManager fm, View container, int tabContentId)
	{
		super(container, tabContentId);
		this.tabContentId = tabContentId;
		refFragmentManager = new WeakReference<FragmentManager>(fm);
	}

	public FragmentTabHolder(FragmentActivity activity, View container, int tabContentId)
	{
		super(container, tabContentId);
		this.tabContentId = tabContentId;
		refFragmentManager = new WeakReference<FragmentManager>(activity.getSupportFragmentManager());
	}
	
	public FragmentTabHolder(Fragment containerFragment, int tabContentId)
	{
		super(containerFragment.getView(), tabContentId);
		this.tabContentId = tabContentId;
		refFragmentManager = new WeakReference<FragmentManager>(containerFragment.getFragmentManager());
	}
}
