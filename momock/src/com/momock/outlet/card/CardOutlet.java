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

import android.view.View;
import android.widget.FrameLayout;

import com.momock.data.IDataList;
import com.momock.holder.ViewHolder;
import com.momock.outlet.Outlet;
import com.momock.util.Logger;

public class CardOutlet extends Outlet<ICardPlug, ViewHolder> implements ICardOutlet<ViewHolder>{

	@Override
	public void onActivate(ICardPlug plug) {
		if (plug.getComponent() != null){
			FrameLayout container = ((FrameLayout)getAttachedObject().getView());
			for(int i = 0; i < container.getChildCount(); i++){
				container.getChildAt(i).setVisibility(View.GONE);
			}
			View cv = ((ViewHolder)plug.getComponent()).getView();
			if (cv.getParent() != container)
				container.addView(cv);
			cv.bringToFront();
			cv.setVisibility(View.VISIBLE);
		} else {
			Logger.debug("The active plug in CardOutlet has not been attached!");
		}
	}

	@Override
	public void onAttach(ViewHolder target) {
		Logger.check(target.getView() instanceof FrameLayout, "The CardOutlet must be used with a FrameLayout!");
		IDataList<ICardPlug> plugs = getPlugs();
		for(int i = 0; i < plugs.getItemCount(); i++){
			ICardPlug plug = plugs.getItem(i);
			Logger.check(plug.getComponent() instanceof ViewHolder, "The plug of CardOutlet must include a ViewHolder!");
			((ViewHolder)plug.getComponent()).reset(); 
			if (plug == this.getActivePlug()){
				onActivate(plug);
			}
		}
	}

}
