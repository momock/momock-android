package com.momock.binder;

import android.view.View;
import android.view.ViewGroup;

public abstract class ItemViewBinder extends ViewBinder {
	protected abstract View onCreateItemView(View convertView, int index, Object item, ViewGroup parent);
}
