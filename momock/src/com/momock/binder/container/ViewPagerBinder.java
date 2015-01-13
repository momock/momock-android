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
package com.momock.binder.container;

import java.lang.ref.WeakReference;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.momock.binder.ContainerBinder;
import com.momock.binder.IItemBinder;
import com.momock.data.DataChangedEventArgs;
import com.momock.data.IDataChangedAware;
import com.momock.data.IDataList;
import com.momock.event.EventArgs;
import com.momock.event.IEventHandler;
import com.momock.event.ItemEventArgs;
import com.momock.util.Logger;
import com.momock.widget.IIndexIndicator;
import com.momock.widget.IRoundAdapter;
import com.momock.widget.RoundPagerAdapter;

public class ViewPagerBinder extends ContainerBinder<ViewPager>{

	int viewPagerState = ViewPager.SCROLL_STATE_IDLE;
	
	public boolean isDragging(){
		return viewPagerState != ViewPager.SCROLL_STATE_IDLE;
	}
	

	public ViewPagerBinder(IItemBinder binder) {
		super(binder);
	}

	PagerAdapter adapter = null;
	public PagerAdapter getAdapter(){
		return adapter;
	}
	public void onBind(final ViewPager view, final IDataList<?> dataSource){
		if (view != null) {
			if (refIndicator != null && refIndicator.get() != null){
				refIndicator.get().setCount(dataSource.getItemCount());
			}
			view.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
				
				@Override
				public void onPageSelected(int position) {
					position = adapter instanceof IRoundAdapter ? ((IRoundAdapter)adapter).getRealPosition(position) : position;
					Object item = dataSource.getItem(position);
					ItemEventArgs args = new ItemEventArgs(view, position, item);
					itemSelectedEvent.fireEvent(view, args);
					if (refIndicator != null && refIndicator.get() != null){
						refIndicator.get().setCurrentIndex(position);
					}
				}
				
				@Override
				public void onPageScrolled(int position, float positionOffset,
						int positionOffsetPixels) {
					
				}
				
				@Override
				public void onPageScrollStateChanged(int state) {
					viewPagerState = state;
				}
			});
			view.setOnTouchListener(new View.OnTouchListener() {
				GestureDetector detector = null;
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					view.getParent().requestDisallowInterceptTouchEvent(true);
					if (detector == null){
						detector = new GestureDetector(v.getContext(), new GestureDetector.SimpleOnGestureListener(){

							@Override
							public boolean onSingleTapConfirmed(MotionEvent e) {
								Object item = dataSource.getItem(adapter instanceof IRoundAdapter ? ((IRoundAdapter)adapter).getRealPosition(view.getCurrentItem()) : view.getCurrentItem());
								ItemEventArgs args = new ItemEventArgs(view, view.getCurrentItem(), item);
								itemClickedEvent.fireEvent(view, args);
								return true;
							}
							
						});
					}
					return detector.onTouchEvent(event);
				}
			});
			adapter = new PagerAdapter(){
				BlockingQueue<View> savedViews = new LinkedBlockingQueue<View>();
				@Override
				public int getCount() {
					return dataSource.getItemCount();
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
					convertView = itemBinder.onCreateItemView(convertView, position, ViewPagerBinder.this);
					if (convertView != null) convertView.setTag(position);
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
					if (refIndicator != null && refIndicator.get() != null){
						refIndicator.get().setCount(dataSource.getItemCount());
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
			if (round)
				adapter = new RoundPagerAdapter(adapter);
			view.setAdapter(adapter);
			if (dataSource instanceof IDataChangedAware)
				((IDataChangedAware)dataSource).addDataChangedHandler(new IEventHandler<DataChangedEventArgs>(){
	
					@Override
					public void process(Object sender, DataChangedEventArgs args) {
						adapter.notifyDataSetChanged();
					}
					
				});
			if (round)
				view.setCurrentItem(Math.max(100, dataSource.getItemCount() * 100), false);
		}
	}

	WeakReference<IIndexIndicator> refIndicator = null;
	boolean round = false;
	public void bind(final ViewPager view, final IDataList<?> list, final IIndexIndicator indicator, boolean round) {		
		this.refIndicator = new WeakReference<IIndexIndicator>(indicator);
		this.round = round;
		super.bind(view, list);
	}
}
