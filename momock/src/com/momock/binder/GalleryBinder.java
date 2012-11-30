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

import android.widget.Gallery;

import com.momock.data.IDataList;
import com.momock.event.IEventHandler;
import com.momock.event.ItemEventArgs;
import com.momock.widget.IndexIndicator;

@SuppressWarnings("deprecation")
public class GalleryBinder extends AdapterViewBinder<Gallery> {
	IndexIndicator indicator;
	public GalleryBinder(ItemViewBinder binder) {
		super(binder);
		this.getItemSelectedEvent().addEventHandler(new IEventHandler<ItemEventArgs>(){

			@Override
			public void process(Object sender,
					ItemEventArgs args) {
				if (indicator != null)
					indicator.setCurrentIndex(args.getIndex());
			}
			
		});
	}
	@Override
	public void bind(Gallery view, IDataList<?> list) {
		super.bind(view, list);
		if (indicator != null){
			indicator.setCount(list.getItemCount());
		}
	}
	public void bind(Gallery view, IDataList<?> list, IndexIndicator indicator){
		this.indicator = indicator;
		bind(view, list);
	}
	

}
