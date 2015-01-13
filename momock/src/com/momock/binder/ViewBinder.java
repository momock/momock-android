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
import android.widget.ImageView;
import android.widget.TextView;

import com.momock.app.IApplication;
import com.momock.data.IDataMap;
import com.momock.holder.ImageHolder;
import com.momock.service.IImageService;
import com.momock.util.BeanHelper;
import com.momock.util.Convert;
import com.momock.util.Logger;

public class ViewBinder {
	public static interface Setter {
		boolean onSet(View view, String viewProp, int index, String key, Object val, View parent, IContainerBinder container);
	}
	static IImageService theImageService = null;
	public static void onStaticCreate(IApplication app){
		theImageService = app.getObjectToInject(IImageService.class);
	}
	public static void onStaticDestroy(IApplication app){
		
	}
	
	static List<Setter> globalSetters = new ArrayList<Setter>();

	public static void addGlobalSetter(Setter setter) {
		globalSetters.add(0, setter);
	}

	static {
		addGlobalSetter(new Setter() {
			@Override
			public boolean onSet(View view, String viewProp, int index, String key, Object val, View parent, IContainerBinder container) {
				if (view instanceof TextView
						&& ("Text".equals(viewProp) || viewProp == null)) {
					((TextView) view).setText(Convert.toString(val));
					return true;
				}
				return false;
			}
		});
		addGlobalSetter(new Setter() {
			@Override
			public boolean onSet(View view, String viewProp, int index, String key, Object val, View parent, IContainerBinder container) {
				if (view instanceof ImageView) {
					if (viewProp == null) {
						if (val instanceof CharSequence) {
							Logger.check(theImageService != null, "The ImageService must not be null!");
							String uri = val.toString();
							Bitmap bmp = theImageService.loadBitmap(uri);
							if (bmp != null) {
								((ImageView) view).setImageBitmap(bmp);
							} else {
								int pos = key.indexOf('|');
								if (pos == -1)
									((ImageView) view).setImageBitmap(null);
								else
									((ImageView) view).setImageBitmap(ImageHolder.get(Convert.toInteger(key.substring(pos + 1).trim())).getAsBitmap());
								if (container != null)
									theImageService.bind(uri, container, index);
								else
									theImageService.bind(uri, (ImageView) view);
							}
						} else {
							if (val instanceof Drawable) {
								((ImageView) view).setImageDrawable((Drawable) val);
							} else {
								((ImageView) view).setImageBitmap(val instanceof ImageHolder ? ((ImageHolder) val).getAsBitmap() : (Bitmap) val);
							}
						}
						return true;
					} else if ("ImageDrawable".equals(viewProp)) {
						((ImageView) view).setImageDrawable(val instanceof ImageHolder ? ((ImageHolder) val).getAsDrawable() : (Drawable) val);
						return true;
					} else if ("ImageBitmap".equals(viewProp)) {
						((ImageView) view).setImageBitmap(val instanceof ImageHolder ? ((ImageHolder) val).getAsBitmap() : (Bitmap) val);
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

	public void bind(View view, int index) {
		bind(view, index, null);
	}

	@SuppressWarnings("unchecked")
	public void bind(View view, int index, IContainerBinder container) {
		Object target = container.getDataSource().getItem(index);
		IDataMap<String, Object> map = null;
		if (target instanceof IDataMap)
			map = (IDataMap<String, Object>) target;
		for (PropView pv : relations) {
			String name = pv.propName;
			Object tagOrId = pv.viewIdOrTag;
			Object val = null;
			String key = name;
			int pos = key.indexOf('|');
			if (pos != -1){
				key = key.substring(0, pos).trim();
			}
			if (map != null && map.hasProperty(key))
				val = map.getProperty(key);
			if (val == null)
				val = BeanHelper.getProperty(target, key, null);
			View cv = null;
			boolean set = false;
			if (tagOrId instanceof String)
				cv = view.findViewWithTag(tagOrId.toString());
			else
				cv = view.findViewById(Convert.toInteger(tagOrId));
			for (Setter s : customSetters) {
				set = s.onSet(cv, pv.viewProp, index, name, val, view, container);
				if (set)
					break;
			}
			if (!set) {
				for (Setter s : globalSetters) {
					set = s.onSet(cv, pv.viewProp, index, name, val, view, container);
					if (set)
						break;
				}
			}
		}
	}
}
