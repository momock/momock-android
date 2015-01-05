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

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.momock.app.IApplication;
import com.momock.app.ICase;
import com.momock.service.ILayoutInflaterService;
import com.momock.util.Logger;

public abstract class ViewHolder implements IComponentHolder{
	static ILayoutInflaterService theLayoutInflaterService = null;
	public static void onStaticCreate(IApplication app){
		theLayoutInflaterService = app.getObjectToInject(ILayoutInflaterService.class);
	}
	public static void onStaticDestroy(IApplication app){
		
	}
	public static interface OnViewCreatedHandler
	{
		void onViewCreated(View view);
	}
	public abstract <T extends View> T getView();
	public abstract void reset();

	public static ViewHolder get(Fragment fragment)	{
		return get(fragment.getView());
	}

	public static ViewHolder get(Fragment fragment, int id)	{
		return get(fragment.getView(), id);
	}
	
	public static ViewHolder get(View view)	{
		final WeakReference<View> refView = new WeakReference<View>(view);
		return new ViewHolder()
		{

			@SuppressWarnings("unchecked")
			@Override
			public <T extends View> T getView() {
				return (T)refView.get();
			}

			@Override
			public void reset() {
			}
			
		};
	}

	public static ViewHolder get(View parentView, final int id)
	{
		final WeakReference<View> refView = new WeakReference<View>(parentView);
		return new ViewHolder()
		{
			WeakReference<View> refChild = null;
			@SuppressWarnings("unchecked")
			@Override
			public <T extends View> T getView() {
				if (refView.get() != null && refChild == null){
					refChild = new WeakReference<View>(refView.get().findViewById(id));
				}
				return (T)(refChild == null ? null : refChild.get());
			}

			@Override
			public void reset() {
				refChild = null;
			}
		};
	}
	public static ViewHolder get(Activity activity, final int id)
	{
		final WeakReference<Activity> refActivity = new WeakReference<Activity>(activity);
		return new ViewHolder()
		{
			WeakReference<View> refChild = null;
			@SuppressWarnings("unchecked")
			@Override
			public <T extends View> T getView() {
				if (refActivity.get() != null && refChild == null){
					refChild = new WeakReference<View>(refActivity.get().findViewById(id));
				}
				return (T)(refChild == null ? null : refChild.get());
			}

			@Override
			public void reset() {
				refChild = null;
			}
		};
	}

	public static ViewHolder get(View parentView, final String tag)
	{
		final WeakReference<View> refView = new WeakReference<View>(parentView);
		return new ViewHolder()
		{
			WeakReference<View> refChild = null;
			@SuppressWarnings("unchecked")
			@Override
			public <T extends View> T getView() {
				if (refView.get() != null && refChild == null){
					refChild = new WeakReference<View>(refView.get().findViewWithTag(tag));
				}
				return (T)(refChild == null ? null : refChild.get());
			}
			
			@Override
			public void reset() {
				refChild = null;
			}			
		};
	}
	public static ViewHolder create(ViewGroup parent, final int resourceId){
		return create(parent, resourceId, null);
	}
	public static ViewHolder create(ViewGroup parent, final int resourceId, final OnViewCreatedHandler handler){
		final WeakReference<ViewGroup> refParent = new WeakReference<ViewGroup>(parent);
		return new ViewHolder()
		{
			WeakReference<View> ref = null;
			@SuppressWarnings("unchecked")
			@Override
			public <T extends View> T getView() {				
				if (ref == null || ref.get() == null)
				{
					Logger.check(theLayoutInflaterService != null, "The LayoutInflaterService must not be null!");
					LayoutInflater inflater = theLayoutInflaterService.getLayoutInflater(refParent.get().getContext());
					ref = new WeakReference<View>(inflater.inflate(resourceId, refParent.get(), false));
					if (handler != null)
						handler.onViewCreated(ref.get());
				}
				return (T)ref.get();
			}
			
			@Override
			public void reset() {
				ref = null;
			}
		};
	}
	public static ViewHolder create(Context context, final int resourceId){
		return create(context, resourceId, null);
	}
	public static ViewHolder create(Context context, final int resourceId, final OnViewCreatedHandler handler){
		final WeakReference<Context> refContext = new WeakReference<Context>(context);
		return new ViewHolder()
		{
			WeakReference<View> ref = null;
			@SuppressWarnings("unchecked")
			@Override
			public <T extends View> T getView() {				
				if (ref == null || ref.get() == null)
				{
					Logger.check(theLayoutInflaterService != null, "The LayoutInflaterService must not be null!");
					LayoutInflater inflater = theLayoutInflaterService.getLayoutInflater(refContext.get());
					ref = new WeakReference<View>(inflater.inflate(resourceId, null));
					if (handler != null)
						handler.onViewCreated(ref.get());
				}
				return (T)ref.get();
			}
			
			@Override
			public void reset() {
				ref = null;
			}
		};
	}
	

	public static ViewHolder create(ICase<?> kase, final int resourceId){
		return create(kase, resourceId, null);
	}
	public static ViewHolder create(final ICase<?> kase, final int resourceId, final OnViewCreatedHandler handler){
		return new ViewHolder()
		{
			WeakReference<View> ref = null;
			@SuppressWarnings("unchecked")
			@Override
			public <T extends View> T getView() {				
				if (ref == null || ref.get() == null)
				{
					Object obj = kase.getAttachedObject();
					Logger.check(obj instanceof Activity || obj instanceof Fragment, "Case must be attached to either an Activity or a Fragment");
					Logger.check(theLayoutInflaterService != null, "The LayoutInflaterService must not be null!");
					LayoutInflater inflater = theLayoutInflaterService.getLayoutInflater(obj instanceof Activity ? (Activity)obj : ((Fragment)obj).getActivity());
					ref = new WeakReference<View>(inflater.inflate(resourceId, null));
					if (handler != null)
						handler.onViewCreated(ref.get());
				}
				return (T)ref.get();
			}
			
			@Override
			public void reset() {
				ref = null;
			}
		};
	}
}
