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

import com.momock.data.IDataMap;
import com.momock.holder.ImageHolder;
import com.momock.holder.TextHolder;
import com.momock.util.BeanHelper;
import com.momock.util.Convert;

public class ViewBinder {
	public static interface Setter{
		boolean onSet(View view, String viewProp, Object val);
	}
	static List<Setter> setters = new ArrayList<Setter>();
	public static void addSetter(Setter setter){
		setters.add(setter);
	}
	static{
		addSetter(new Setter(){
			@Override
			public boolean onSet(View view, String viewProp, Object val) {
				if (view instanceof TextView && ("Text".equals(viewProp) || viewProp == null && val instanceof TextHolder)){
					((TextView)view).setText(val instanceof TextHolder ? ((TextHolder)val).getText() : Convert.toString(val));
					return true;
				}
				return false;
			}			
		});
		addSetter(new Setter(){
			@Override
			public boolean onSet(View view, String viewProp, Object val) {
				if (view instanceof ImageView){
					if ("ImageDrawable".equals(viewProp) || viewProp == null && val instanceof ImageHolder)
					{
						((ImageView)view).setImageDrawable(val instanceof ImageHolder ? ((ImageHolder)val).getAsDrawable() : (Drawable)val);	
						return true;
					}
					if ("ImageBitmap".equals(viewProp))
					{
						((ImageView)view).setImageBitmap(val instanceof ImageHolder ? ((ImageHolder)val).getAsBitmap() : (Bitmap)val);	
						return true;
					}
				}
				return false;
			}			
		});
	}
	class PropView
	{
		public String propName;
		public String viewProp;
		public Object viewIdOrTag;
		public PropView(String propName, Object viewIdOrTag, String viewProp)
		{
			this.propName = propName;
			this.viewIdOrTag = viewIdOrTag;
			this.viewProp = viewProp;
		}
	}
	List<PropView> relations = new ArrayList<PropView>();
	public ViewBinder link(String name, int resourceId){
		relations.add(new PropView(name, resourceId, null));
		return this;
	}
	public ViewBinder link(String name, String tag){
		relations.add(new PropView(name, tag, null));
		return this;
	}
	public ViewBinder link(String name, int resourceId, String viewProp){
		relations.add(new PropView(name, resourceId, viewProp));
		return this;
	}
	public ViewBinder link(String name, String tag, String viewProp){
		relations.add(new PropView(name, tag, viewProp));
		return this;
	}
	@SuppressWarnings("unchecked")
	public void bind(View view, Object target){
		IDataMap<String, Object> map = null;
		if (map instanceof IDataMap)
			map = (IDataMap<String, Object>)target;
		for(PropView pv : relations){
			String name = pv.propName;
			Object tagOrId = pv.viewIdOrTag;
			Object val = null;
			if (map != null && map.hasProperty(name))
				val = map.getProperty(name);
			if (val == null)
				val = BeanHelper.getProperty(target, name, null);
			if (val != null){
				View cv = null;
				if (tagOrId instanceof String)
					cv = view.findViewWithTag(tagOrId.toString());
				else
					cv = view.findViewById(Convert.toInteger(tagOrId));
				for(Setter s : setters){
					if (s.onSet(cv, pv.viewProp, val)) break;
				}
			}
		}
	}
}
