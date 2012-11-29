package com.momock.widget;

import android.view.View;
import android.widget.Adapter;

public interface IPlainAdapterView {
	void setAdapter(Adapter adapter);
	void setOnItemClickListener(OnItemClickListener listener);
    public interface OnItemClickListener {
        void onItemClick(IPlainAdapterView parent, View view, int index);
    }

}
