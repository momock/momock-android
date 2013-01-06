package com.momock.binder;

import java.lang.ref.WeakReference;

import android.view.View;
import android.view.ViewGroup;

import com.momock.data.IDataList;
import com.momock.event.Event;
import com.momock.event.EventArgs;
import com.momock.event.IEvent;
import com.momock.event.ItemEventArgs;

public abstract class ContainerBinder<T extends ViewGroup> implements IContainerBinder {
	protected IItemBinder itemBinder;
	protected IDataList<?> dataSource;
	protected WeakReference<ViewGroup> refContainerView = null;
	protected IEvent<ItemEventArgs> itemClickedEvent = new Event<ItemEventArgs>();
	protected IEvent<ItemEventArgs> itemSelectedEvent = new Event<ItemEventArgs>();
	protected IEvent<EventArgs> dataChangedEvent = new Event<EventArgs>();

	public ContainerBinder(IItemBinder itemBinder){
		this.itemBinder = itemBinder;
	}
	@Override
	public IDataList<?> getDataSource() {
		return dataSource;
	}
	@Override
	public IEvent<EventArgs> getDataChangedEvent() {
		return dataChangedEvent;
	}

	@Override
	public IEvent<ItemEventArgs> getItemClickedEvent() {
		return itemClickedEvent;
	}

	@Override
	public IEvent<ItemEventArgs> getItemSelectedEvent() {
		return itemSelectedEvent;
	}

	@Override
	public View getViewOf(Object item) {
		ViewGroup parent = getContainerView();
		if (parent != null){
			for(int i = 0; i < parent.getChildCount(); i++){
				View c = parent.getChildAt(i);
				if (c.getTag() == item) return c;
			}
		}
		return null;
	}

	protected abstract void onBind(T containerView, IDataList<?> dataSource);
	
	@SuppressWarnings("unchecked")
	@Override
	public void bind(ViewGroup containerView, IDataList<?> dataSource) {
		this.dataSource = dataSource;
		this.refContainerView = new WeakReference<ViewGroup>(containerView);
		onBind((T)containerView, dataSource);
	}

	@Override
	public ViewGroup getContainerView() {
		return refContainerView == null ? null : refContainerView.get();
	}

	@Override
	public IItemBinder getItemBinder() {
		return itemBinder;
	}

}
