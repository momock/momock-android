package com.momock.binder;

import android.view.View;
import android.view.ViewGroup;

import com.momock.data.IDataList;
import com.momock.event.EventArgs;
import com.momock.event.IEvent;
import com.momock.event.ItemEventArgs;

public interface IContainerBinder {
	IDataList<?> getDataSource();	
	ViewGroup getContainerView();
	View getViewOf(Object item);
	IItemBinder getItemBinder();
	void bind(ViewGroup containerView, final IDataList<?> dataSource);
	IEvent<EventArgs> getDataChangedEvent();
	IEvent<ItemEventArgs> getItemClickedEvent();
	IEvent<ItemEventArgs> getItemSelectedEvent();
}
