package com.momock.binder;

import android.widget.Gallery;

import com.momock.data.IDataList;
import com.momock.event.IEventHandler;
import com.momock.event.ItemEventArgs;
import com.momock.widget.IndexIndicator;

@SuppressWarnings("deprecation")
public class GalleryBinder extends AdapterViewBinder<Gallery> {
	IndexIndicator indicator;
	public GalleryBinder(ItemViewBinder binder) {
		super(binder);
		this.getItemSelectedEvent().addEventHandler(new IEventHandler<ItemEventArgs>(){

			@Override
			public void process(Object sender,
					ItemEventArgs args) {
				if (indicator != null)
					indicator.setCurrentIndex(args.getIndex());
			}
			
		});
	}
	@Override
	public void bind(Gallery view, IDataList<?> list) {
		super.bind(view, list);
		if (indicator != null){
			indicator.setCount(list.getItemCount());
		}
	}
	public void bind(Gallery view, IDataList<?> list, IndexIndicator indicator){
		this.indicator = indicator;
		bind(view, list);
	}
	

}
