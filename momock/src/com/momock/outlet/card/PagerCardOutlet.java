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

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import com.momock.data.IDataList;
import com.momock.holder.ViewHolder;
import com.momock.outlet.IPlug;
import com.momock.outlet.Outlet;
import com.momock.util.Logger;

public class PagerCardOutlet extends Outlet implements ICardOutlet{	
	WeakReference<ViewPager> refTarget = null;

	public void attach(ViewHolder target) {
		Logger.check(target.getView() instanceof ViewPager, "Parameter type error!");
		attach((ViewPager)target.getView());
	}
	public void attach(ViewPager target) {
		refTarget = new WeakReference<ViewPager>(target);
		ViewPager pager = target;
		pager.setAdapter(new PagerAdapter(){

			@Override
			public int getCount() {
				return getPlugs().getItemCount();
			}

			@Override
			public boolean isViewFromObject(View view, Object object) {
				return view == object;
			}

			@Override
			public Object instantiateItem(ViewGroup container, int position) {
				ICardPlug plug = (ICardPlug)getPlugs().getItem(position);
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
		IDataList<IPlug> plugs = getPlugs();
		for(int i = 0; i < plugs.getItemCount(); i++){
			ICardPlug plug = (ICardPlug)plugs.getItem(i);
			Logger.check(plug.getComponent() instanceof ViewHolder, "The plug of PagerCardOutlet must include a ViewHolder!");
			((ViewHolder)plug.getComponent()).reset(); 
			if (plug == this.getActivePlug()){
				onActivate(plug);
			}
		}
	}

	@Override
	public void onActivate(IPlug plug) {
		if (((ICardPlug)plug).getComponent() != null){
			ViewPager pager = refTarget.get();
			pager.setCurrentItem(getIndexOf(plug), true);
		} else {
			Logger.debug("The active plug in PagerCardOutlet has not been attached!");
		}
	}
	
}
