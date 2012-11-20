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
package com.momock.holder;

import java.lang.ref.WeakReference;

import com.momock.app.App;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

public abstract class ViewHolder implements IComponentHolder{
	public static interface OnCreateViewHandler
	{
		void onCreateView(View view);
	}
	OnCreateViewHandler handler = null;
	public abstract View getView();
	public ViewHolder setOnCreateViewHandler(OnCreateViewHandler handler)
	{
		this.handler = handler;
		return this;
	}
	public static ViewHolder get(final View view)
	{
		return new ViewHolder()
		{

			@Override
			public View getView() {
				return view;
			}
			
		};
	}
	public static ViewHolder get(final int resourceId)
	{
		return get(App.get(), resourceId);
	}
	public static ViewHolder get(final Context context, final int resourceId)
	{
		return new ViewHolder()
		{
			WeakReference<View> ref = null;
			@Override
			public View getView() {
				if (ref == null || ref.get() == null)
				{
					LayoutInflater inflater = App.get().getLayoutInflater(context);
					ref = new WeakReference<View>(inflater.inflate(resourceId, null));
					if (handler != null)
						handler.onCreateView(ref.get());
				}
				return ref.get();
			}
			
		};
	}
}
