package com.momock.event;

import android.view.View;

public class ItemEventArgs extends EventArgs {
	int index;
	Object item;
	View view;

	public ItemEventArgs(View view, int index, Object item) {
		this.view = view;
		this.index = index;
		this.item = item;
	}

	public int getIndex() {
		return index;
	}

	public Object getItem() {
		return item;
	}

	public View getView() {
		return view;
	}
}
