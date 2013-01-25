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
package com.momock.service;

import java.util.HashMap;
import java.util.Map;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import com.momock.util.Logger;

public class LayoutInflaterService implements ILayoutInflaterService {
	public class CustomLayoutInflater extends android.view.LayoutInflater
			implements Cloneable {

		public CustomLayoutInflater(LayoutInflater original, Context newContext) {
			super(original, newContext);
		}

		protected CustomLayoutInflater(Context context) {
			super(context);
		}

		@Override
		public LayoutInflater cloneInContext(Context newContext) {
			return new CustomLayoutInflater(this, newContext);
		}

		@Override
		protected View onCreateView(String name, AttributeSet attrs)
				throws ClassNotFoundException {
			if (shortNames.containsKey(name)) {
				try {
					return createView(name, shortNames.get(name) + ".", attrs);
				} catch (Exception e) {
					Logger.error(e);
				}
			}
			try {
				return createView(name, "android.widget.", attrs);
			} catch (ClassNotFoundException e) {
				return createView(name, "android.view.", attrs);
			}
		}

		@TargetApi(Build.VERSION_CODES.HONEYCOMB)
		@Override
		protected View onCreateView(View parent, String name, AttributeSet attrs)
				throws ClassNotFoundException {
			if (shortNames.containsKey(name)) {
				try {
					return createView(name, shortNames.get(name) + ".", attrs);
				} catch (Exception e) {
					Logger.error(e);
				}
			}
			try {
				return createView(name, "android.widget.", attrs);
			} catch (ClassNotFoundException e) {
				return createView(name, "android.view.", attrs);
			}
		}
	}

	Map<String, String> shortNames = new HashMap<String, String>();

	@Override
	public void registerShortName(String prefix, String... classess) {
		for (String clazz : classess) {
			shortNames.put(clazz, prefix);
		}
	}

	@Override
	public LayoutInflater getLayoutInflater(Context context) {
		LayoutInflater layoutInflater = new CustomLayoutInflater(
				LayoutInflater.from(context), context);
		return layoutInflater;
	}

	@Override
	public LayoutInflater getLayoutInflater(LayoutInflater inflater) {
		if (!(inflater instanceof CustomLayoutInflater))
			inflater = new CustomLayoutInflater(inflater, inflater.getContext());
		return inflater;
	}

	@Override
	public Class<?>[] getDependencyServices() {
		return null;
	}

	@Override
	public void start() {

	}

	@Override
	public void stop() {
		shortNames.clear();
	}

	@Override
	public boolean canStop() {
		return true;
	}

}
