/*******************************************************************************
 * Copyright 2013 momock.com
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

import android.view.View;
import android.view.ViewGroup;

import com.momock.data.IDataList;
import com.momock.event.EventArgs;
import com.momock.event.IEvent;

public interface IContainerBinder {
	IDataList<?> getDataSource();	
	ViewGroup getContainerView();
	View getViewOf(Object item);
	IItemBinder getItemBinder();
	void bind(ViewGroup containerView, final IDataList<?> dataSource);
	IEvent<EventArgs> getDataChangedEvent();
}