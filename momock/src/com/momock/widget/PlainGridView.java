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

public class PlainGridView extends LinearLayout implements IPlainAdapterView {
	Adapter adapter;
	Observer observer = new Observer(this);
	int columns = 2;
	int rowHeight = 0;
	OnItemClickListener listener;
	public PlainGridView(Context context) {
		super(context);
		init();
	}

	public PlainGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public PlainGridView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	void init() {
		this.setOrientation(LinearLayout.VERTICAL);
	}

	public int getNumColumns() {
		return columns;
	}

	public void setNumColumns(int columns) {
		this.columns = columns;
		observer.onChanged();
	}

	public void setOnItemClickListener(OnItemClickListener listener){
		this.listener = listener;
	}
	public void setAdapter(Adapter adapter) {
		if (this.adapter != null)
			this.adapter.unregisterDataSetObserver(observer);

		this.adapter = adapter;
		adapter.registerDataSetObserver(observer);
		observer.onChanged();
	}
	class DummyGridItem extends View{

		public DummyGridItem(Context context, AttributeSet attrs, int defStyle) {
			super(context, attrs, defStyle);
		}

		public DummyGridItem(Context context, AttributeSet attrs) {
			super(context, attrs);
		}

		public DummyGridItem(Context context) {
			super(context);
		}
		
	}
	private class Observer extends DataSetObserver {
		PlainGridView context;

		public Observer(PlainGridView context) {
			this.context = context;
		}

		@Override
		public void onChanged() {
			if (context.adapter == null)
				return;

			int i;
			int count = context.getChildCount();
			int rows = (count + columns - 1) / columns;
			List<LinearLayout> oldRows = new ArrayList<LinearLayout>(rows);
			List<View> oldItems = new ArrayList<View>(count);
			List<View> oldDummyItems = new ArrayList<View>(columns);

			for (i = 0; i < context.getChildCount(); i++) {
				LinearLayout row = (LinearLayout) context.getChildAt(i);
				oldRows.add(row);
				for (int j = 0; j < row.getChildCount(); j++) {
					View v = row.getChildAt(j);
					if (i == context.getChildCount() - 1 && v instanceof DummyGridItem)
						oldDummyItems.add(v);
					else
						oldItems.add(v);
					
				}
				row.removeAllViews();
			}
			context.removeAllViews();

			Iterator<LinearLayout> iterRow = oldRows.iterator();
			Iterator<View> iterItem = oldItems.iterator();
			LinearLayout curr = null;
			for (i = 0; i < context.adapter.getCount(); i++) {
				final int index = i;
				int c = i % columns;
				if (c == 0) {
					curr = iterRow.hasNext() ? iterRow.next() : null;
					if (curr == null) {
						curr = new LinearLayout(context.getContext());
						curr.setLayoutParams(new LinearLayout.LayoutParams(
								LinearLayout.LayoutParams.MATCH_PARENT,
								LinearLayout.LayoutParams.WRAP_CONTENT));
					}
					curr.setOrientation(LinearLayout.HORIZONTAL);
					curr.setWeightSum(columns);
					context.addView(curr);
				}
				View convertView = iterItem.hasNext() ? iterItem.next() : null;
				convertView = context.adapter.getView(i, convertView, curr);
				LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) convertView
						.getLayoutParams();
				if (lp == null) {
					lp = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
					convertView.setLayoutParams(lp);
				} else {
					lp.weight = 1;
				}
				if (rowHeight > 0)
					lp.height = rowHeight;
				if (convertView.getParent() == null)
					curr.addView(convertView);

				convertView.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						listener.onItemClick(PlainGridView.this, v, index);
					}
				});
			}

			Iterator<View> iterDummyItem = oldDummyItems.iterator();
			i = context.adapter.getCount() % columns;
			if (i > 0){
				for (; i < columns; i++) {
					View convertView = iterDummyItem.hasNext() ? iterDummyItem.next() : null;
					if (convertView == null){
						convertView = new DummyGridItem(context.getContext());
					}
					
					curr.addView(convertView);
					convertView.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1));
				}
			}
			super.onChanged();
		}

		@Override
		public void onInvalidated() {
			context.removeAllViews();
			super.onInvalidated();
		}
	}

	public int getRowHeight() {
		return rowHeight;
	}

	public void setRowHeight(int rowHeight) {
		this.rowHeight = rowHeight;
	}

	@Override
	public Adapter getAdapter() {
		return adapter;
	}

}
