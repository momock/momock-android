package com.momock.binder;

import android.view.View;

public interface IItemBinder {
	View onCreateItemView(View convertView, Object item, IContainerBinder container);
}
