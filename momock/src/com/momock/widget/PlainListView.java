package com.momock.widget;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.annotation.TargetApi;
import android.content.Context;
import android.database.DataSetObserver;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Adapter;
import android.widget.LinearLayout;

public class PlainListView extends LinearLayout implements IPlainAdapterView{
	Adapter adapter;
	Observer observer = new Observer(this);

	public PlainListView(Context context) {
		super(context);
		init();
	}

	public PlainListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public PlainListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	void init(){
		this.setOrientation(LinearLayout.VERTICAL);
	}
	public void setAdapter(Adapter adapter) {
		if (this.adapter != null)
			this.adapter.unregisterDataSetObserver(observer);

		this.adapter = adapter;
		adapter.registerDataSetObserver(observer);
		observer.onChanged();
	}

	private class Observer extends DataSetObserver {
		PlainListView context;

		public Observer(PlainListView context) {
			this.context = context;
		}

		@Override
		public void onChanged() {
			List<View> oldViews = new ArrayList<View>(context.getChildCount());

			for (int i = 0; i < context.getChildCount(); i++)
				oldViews.add(context.getChildAt(i));

			Iterator<View> iter = oldViews.iterator();

			context.removeAllViews();

			for (int i = 0; i < context.adapter.getCount(); i++) {
				View convertView = iter.hasNext() ? iter.next() : null;
				context.addView(context.adapter
						.getView(i, convertView, context));
			}
			super.onChanged();
		}

		@Override
		public void onInvalidated() {
			context.removeAllViews();
			super.onInvalidated();
		}
	}

}
