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
package com.momock.widget;

import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

public class RoundPagerAdapter extends PagerAdapter implements IRoundAdapter{

	private PagerAdapter adapter;

	public RoundPagerAdapter(PagerAdapter adapter) {
		this.adapter = adapter;
	}

	@Override
	public int getCount() {
		return Integer.MAX_VALUE;
	}
	@Override
	public int getRealPosition(int position){
		return position % getRealCount();
	}
	@Override
	public int getRealCount() {
		return adapter.getCount();
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		return adapter.instantiateItem(container, getRealPosition(position));
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		adapter.destroyItem(container, getRealPosition(position), object);
	}

	@Override
	public void finishUpdate(ViewGroup container) {
		adapter.finishUpdate(container);
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return adapter.isViewFromObject(view, object);
	}

	@Override
	public void restoreState(Parcelable bundle, ClassLoader classLoader) {
		adapter.restoreState(bundle, classLoader);
	}

	@Override
	public Parcelable saveState() {
		return adapter.saveState();
	}

	@Override
	public void startUpdate(ViewGroup container) {
		adapter.startUpdate(container);
	}

	@Override
	public void setPrimaryItem(ViewGroup container, int position, Object object) {		
		adapter.setPrimaryItem(container, position, object);
	}

	@Override
	public int getItemPosition(Object object) {
		return adapter.getItemPosition(object);
	}

	@Override
	public void notifyDataSetChanged() {
		adapter.notifyDataSetChanged();
	}

	@Override
	public CharSequence getPageTitle(int position) {
		return adapter.getPageTitle(position);
	}

}
