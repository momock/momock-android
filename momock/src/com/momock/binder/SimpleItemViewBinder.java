package com.momock.binder;

import android.view.View;
import android.view.ViewGroup;

import com.momock.holder.ViewHolder;
import com.momock.util.Logger;

public class SimpleItemViewBinder extends ItemViewBinder{
	int itemViewId;
	public SimpleItemViewBinder(int itemViewId, int[] childViewIds, String[] props){
		Logger.check(childViewIds != null && props != null && childViewIds.length == props.length, "Parameter error!");
		this.itemViewId = itemViewId;
		for(int i = 0; i < props.length; i++){
			link(props[i], childViewIds[i]);
		}
	}
	@Override
	protected View onCreateItemView(View convertView, int index,
			Object plug, ViewGroup parent) {
		View view = convertView;
		if (view == null) {
			view = ViewHolder.create(parent, itemViewId).getView();
		}
		bind(view, plug);
		return view;
	}

}
