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
package com.momock.binder;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.momock.data.IDataList;
import com.momock.event.Event;
import com.momock.event.EventArgs;
import com.momock.event.IEvent;
import com.momock.event.IEventHandler;
import com.momock.event.ItemEventArgs;
import com.momock.util.Logger;
import com.momock.widget.IndexIndicator;

public class ViewPagerBinder {

	IEvent<ItemEventArgs> itemClickedEvent = new Event<ItemEventArgs>();
	IEvent<ItemEventArgs> itemSelectedEvent = new Event<ItemEventArgs>();
	IEvent<EventArgs> dataChangedEvent = new Event<EventArgs>();

	public IEvent<EventArgs> getDataChangedEvent() {
		return dataChangedEvent;
	}
	
	public IEvent<ItemEventArgs> getItemClickedEvent() {
		return itemClickedEvent;
	}

	public IEvent<ItemEventArgs> getItemSelectedEvent() {
		return itemSelectedEvent;
	}

	public ViewPagerBinder addItemClickedEventHandler(
			IEventHandler<ItemEventArgs> handler) {
		itemClickedEvent.addEventHandler(handler);
		return this;
	}

	protected ItemViewBinder binder;

	public ViewPagerBinder(ItemViewBinder binder) {
		this.binder = binder;
	}

	PagerAdapter adapter = null;
	public PagerAdapter getAdapter(){
		return adapter;
	}
	public void bind(ViewPager view, IDataList<?> list){
		bind(view, list, null);
	}
	public void bind(final ViewPager view, final IDataList<?> list, final IndexIndicator indicator) {
		if (view != null) {
			if (indicator != null){
				indicator.setCount(list.getItemCount());
			}
			view.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
				
				@Override
				public void onPageSelected(int position) {
					ItemEventArgs args = new ItemEventArgs(view, position,
							list.getItem(position));
					itemSelectedEvent.fireEvent(view, args);
					if (indicator != null)
						indicator.setCurrentIndex(position);
				}
				
				@Override
				public void onPageScrolled(int position, float positionOffset,
						int positionOffsetPixels) {
					
				}
				
				@Override
				public void onPageScrollStateChanged(int state) {
					
				}
			});
			view.setOnTouchListener(new View.OnTouchListener() {
				GestureDetector detector = null;
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					if (detector == null){
						detector = new GestureDetector(v.getContext(), new GestureDetector.SimpleOnGestureListener(){

							@Override
							public boolean onSingleTapConfirmed(MotionEvent e) {
								ItemEventArgs args = new ItemEventArgs(view, view.getCurrentItem(),
										list.getItem(view.getCurrentItem()));
								itemClickedEvent.fireEvent(view, args);
								return true;
							}
							
						});
					}
					detector.onTouchEvent(event);
					return false;
				}
			});
			adapter = new PagerAdapter(){
				BlockingQueue<View> savedViews = new LinkedBlockingQueue<View>();
				@Override
				public int getCount() {
					return list.getItemCount();
				}

				@Override
				public boolean isViewFromObject(View view, Object object) {					
					return view == ((View) object);
				}

				@Override
				public Object instantiateItem(ViewGroup container, int position) {
					View convertView = null;
					if (savedViews.size() > 0){
						convertView = savedViews.poll();
					} 
					convertView = binder.onCreateItemView(convertView, position,
							list.getItem(position), view);
					container.addView(convertView, 0);
					return convertView;
				}

				@Override
				public void destroyItem(ViewGroup container, int position,
						Object object) {
					savedViews.add((View)object);
					container.removeView((View)object);
				}
				long lastDataSetChangedTick = 0;
				@Override
				public void notifyDataSetChanged() {
					lastDataSetChangedTick = System.nanoTime();
					getDataChangedEvent().fireEvent(this, new EventArgs());
					if (indicator != null){
						indicator.setCount(list.getItemCount());
					}
					super.notifyDataSetChanged();
					Logger.debug("ViewPagerBinder.PagerAdapter.notifyDataSetChanged");
				}
				long updateTick = 0;
				@Override
				public void startUpdate(ViewGroup container) {
					updateTick = System.nanoTime();
					super.startUpdate(container);
				}

				@Override
				public void setPrimaryItem(ViewGroup container, int position,
						Object object) {
					super.setPrimaryItem(container, position, object);
				}

				@Override
				public void finishUpdate(ViewGroup container) {
					super.finishUpdate(container);
					if (lastDataSetChangedTick < updateTick)
						lastDataSetChangedTick = 0;
				}

				@Override
				public int getItemPosition(Object object) {
					return lastDataSetChangedTick == 0 ? PagerAdapter.POSITION_UNCHANGED : PagerAdapter.POSITION_NONE;
				}
			};
			view.setAdapter(adapter);
		}
	}
}
