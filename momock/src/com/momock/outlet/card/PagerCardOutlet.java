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

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import com.momock.data.IDataList;
import com.momock.holder.ViewHolder;
import com.momock.outlet.Outlet;
import com.momock.util.Logger;

public class PagerCardOutlet extends Outlet<ICardPlug, ViewHolder>{
	IDataList<ICardPlug> plugs;
	@Override
	public void onAttach(ViewHolder target) {
		Logger.check(target.getView() instanceof ViewPager, "The PagerCardOutlet must be attached to a ViewPager!");
		ViewPager pager = (ViewPager)target.getView();
		plugs = getPlugs();
		for(int i = 0; i < plugs.getItemCount(); i++){
			ICardPlug plug = plugs.getItem(i);
			Logger.check(plug.getComponent() instanceof ViewHolder, "The plug of PagerCardOutlet must include a ViewHolder!");
			((ViewHolder)plug.getComponent()).reset(); 
		}
		pager.setAdapter(new PagerAdapter(){

			@Override
			public int getCount() {
				return plugs.getItemCount();
			}

			@Override
			public boolean isViewFromObject(View view, Object object) {
				return view == object;
			}

			@Override
			public Object instantiateItem(ViewGroup container, int position) {
				ICardPlug plug = plugs.getItem(position);
				View view = ((ViewHolder)plug.getComponent()).getView();
	            container.addView(view);
				return view;
			}

			@Override
			public void destroyItem(ViewGroup container, int position,
					Object object) {
				container.removeView((View)object);
			}
		});
	}

	@Override
	public void onActivate(ICardPlug plug) {
		Logger.check(plug.getComponent() instanceof ViewHolder, "The plug of PagerCardOutlet must include a ViewHolder!");
		ViewPager pager = (ViewPager)getAttachedObject().getView();
		for(int i = 0; i < plugs.getItemCount(); i++){
			if (plugs.getItem(i) == plug){
				pager.setCurrentItem(i, true);
				break;				
			}
		}		
	}
	
}
