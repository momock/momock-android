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

import java.util.ArrayList;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.momock.app.App;
import com.momock.data.IDataMap;
import com.momock.holder.ImageHolder;
import com.momock.holder.ViewHolder;
import com.momock.util.BeanHelper;
import com.momock.util.Convert;

public class ViewBinder {
	public static interface Setter {
		boolean onSet(View view, String viewProp, Object obj, String key,
				Object val, ViewGroup parent);
	}

	static List<Setter> globalSetters = new ArrayList<Setter>();

	public static void addGlobalSetter(Setter setter) {
		globalSetters.add(0, setter);
	}

	static {
		addGlobalSetter(new Setter() {
			@Override
			public boolean onSet(View view, String viewProp, Object obj,
					String key, Object val, ViewGroup parent) {
				if (view instanceof TextView
						&& ("Text".equals(viewProp) || viewProp == null)) {
					((TextView) view).setText(Convert.toString(val));
					return true;
				}
				return false;
			}
		});
		addGlobalSetter(new Setter() {
			@SuppressWarnings("rawtypes")
			@Override
			public boolean onSet(View view, String viewProp, Object obj,
					String key, Object val, ViewGroup parent) {
				if (view instanceof ImageView) {
					if (viewProp == null) {
						if (val instanceof CharSequence) {
							String uri = val.toString();
							ImageHolder ih = ImageHolder.get(uri);
							if (ih != null && ih.getAsBitmap() != null) {
								((ImageView) view).setImageBitmap(ih
										.getAsBitmap());
								return true;
							} else {
								((ImageView) view).setImageBitmap(null);
								if (parent instanceof AdapterView)
									App.get().getImageService().bind(uri, (AdapterView) parent);
								else
									App.get().getImageService().bind(uri, (ImageView) view);
							}
						} else {
							if (val instanceof Drawable) {
								((ImageView) view)
										.setImageDrawable((Drawable) val);
							} else {
								((ImageView) view)
										.setImageBitmap(val instanceof ImageHolder ? ((ImageHolder) val)
												.getAsBitmap() : (Bitmap) val);
							}
						}
						return true;
					} else if ("ImageDrawable".equals(viewProp)) {
						((ImageView) view)
								.setImageDrawable(val instanceof ImageHolder ? ((ImageHolder) val)
										.getAsDrawable() : (Drawable) val);
						return true;
					} else if ("ImageBitmap".equals(viewProp)) {
						((ImageView) view)
								.setImageBitmap(val instanceof ImageHolder ? ((ImageHolder) val)
										.getAsBitmap() : (Bitmap) val);
						return true;
					}
				}
				return false;
			}
		});
	}
	List<Setter> customSetters = new ArrayList<Setter>();

	public void addSetter(Setter setter) {
		customSetters.add(0, setter);
	}

	public void removeSetter(Setter setter) {
		customSetters.remove(setter);
	}

	class PropView {
		public String propName;
		public String viewProp;
		public Object viewIdOrTag;

		public PropView(String propName, Object viewIdOrTag, String viewProp) {
			this.propName = propName;
			this.viewIdOrTag = viewIdOrTag;
			this.viewProp = viewProp;
		}
	}

	List<PropView> relations = new ArrayList<PropView>();

	public ViewBinder link(String name, int resourceId) {
		relations.add(new PropView(name, resourceId, null));
		return this;
	}

	public ViewBinder link(String name, String tag) {
		relations.add(new PropView(name, tag, null));
		return this;
	}

	public ViewBinder link(String name, int resourceId, String viewProp) {
		relations.add(new PropView(name, resourceId, viewProp));
		return this;
	}

	public ViewBinder link(String name, String tag, String viewProp) {
		relations.add(new PropView(name, tag, viewProp));
		return this;
	}

	public void bind(ViewHolder view, Object target) {
		bind(view, target, null);
	}

	public void bind(ViewHolder view, Object target, ViewGroup parent) {
		bind(view.getView(), target, parent);
	}

	public void bind(View view, Object target) {
		bind(view, target, null);
	}

	@SuppressWarnings("unchecked")
	public void bind(View view, Object target, ViewGroup parent) {
		IDataMap<String, Object> map = null;
		if (target instanceof IDataMap)
			map = (IDataMap<String, Object>) target;
		for (PropView pv : relations) {
			String name = pv.propName;
			Object tagOrId = pv.viewIdOrTag;
			Object val = null;
			if (map != null && map.hasProperty(name))
				val = map.getProperty(name);
			if (val == null)
				val = BeanHelper.getProperty(target, name, null);
			View cv = null;
			boolean set = false;
			if (tagOrId instanceof String)
				cv = view.findViewWithTag(tagOrId.toString());
			else
				cv = view.findViewById(Convert.toInteger(tagOrId));
			for (Setter s : customSetters) {
				set = s.onSet(cv, pv.viewProp, target, name, val, (ViewGroup)view);
				if (set)
					break;
			}
			if (!set) {
				for (Setter s : globalSetters) {
					set = s.onSet(cv, pv.viewProp, target, name, val, (ViewGroup)view);
					if (set)
						break;
				}
			}
		}
	}
}
