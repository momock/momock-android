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

import com.momock.binder.IItemBinder;
import com.momock.binder.ItemBinder;

import android.widget.ListView;

public class ListViewBinder extends AdapterViewBinder<ListView> {

	public ListViewBinder(IItemBinder binder) {
		super(binder);
	}

	public static ListViewBinder getSimple(String propName) {
		return new ListViewBinder(new ItemBinder(
				android.R.layout.simple_list_item_1,
				new int[] { android.R.id.text1 }, new String[] { propName }));
	}

}
