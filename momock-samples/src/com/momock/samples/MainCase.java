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
package com.momock.samples;

import android.view.View;
import android.view.ViewGroup;

import com.momock.app.App;
import com.momock.app.Case;
import com.momock.binder.ViewBinder;
import com.momock.holder.ViewHolder;
import com.momock.outlet.action.IActionPlug;
import com.momock.outlet.action.ListViewActionOutlet;

public class MainCase extends Case{

	@Override
	protected void onCreate() {
		final ViewBinder binder = new ViewBinder();
		//binder.link("Text", R.id.sampleItem);
		binder.link("Text", android.R.id.text1);
		App.get().addOutlet(Outlets.SAMPLES, new ListViewActionOutlet(new ListViewActionOutlet.OnCreateItemViewHandler() {
			
			@Override
			public View onBindItemView(View convertView, IActionPlug plug,
					ViewGroup parent) {
				View view = convertView;
				if (view == null){
					//view = ViewHolder.get(R.layout.samples_list_item).getView();
					view = ViewHolder.get(android.R.layout.simple_list_item_1).getView();
				}
				binder.bind(view, plug);
				return view;
			}
		}));
	}

}
