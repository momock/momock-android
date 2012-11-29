package com.momock.binder;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.momock.app.App;
import com.momock.binder.ViewBinder.Setter;
import com.momock.data.IDataList;
import com.momock.holder.ImageHolder;
import com.momock.holder.ViewHolder;
import com.momock.widget.IPlainAdapterView;

public class PlainAdapterViewBinder<T extends IPlainAdapterView> {

	protected ItemViewBinder binder;
	public PlainAdapterViewBinder(ItemViewBinder binder){
		this.binder = binder;
	}
	Setter imageSetter = null;

	@SuppressWarnings("unchecked")
	public void bind(ViewHolder view, IDataList<?> list) {
		bind((T) view.getView(), list);
	}

	public void bind(T view, final IDataList<?> list) {
		if (view != null) {			
			final BaseAdapter adapter = new BaseAdapter() {

				@Override
				public int getCount() {
					return list.getItemCount();
				}

				@Override
				public Object getItem(int position) {
					return list.getItem(position);
				}

				@Override
				public long getItemId(int position) {
					return position;
				}

				@Override
				public View getView(int position, View convertView,
						ViewGroup parent) {
					return binder.onCreateItemView(convertView, position,
							getItem(position), parent);
				}

			};
			if (imageSetter != null)
				binder.removeSetter(imageSetter);
			imageSetter = new Setter() {

				@Override
				public boolean onSet(View view, String viewProp, Object val) {
					if (view instanceof ImageView) {
						if (viewProp == null) {
							if (val instanceof CharSequence) {
								ImageHolder ih = ImageHolder
										.get(val.toString());
								if (ih != null && ih.getAsBitmap() != null) {
									((ImageView) view).setImageBitmap(ih
											.getAsBitmap());
									return true;
								} else {
									App.get()
											.getImageService()
											.load(adapter, (ImageView) view,
													val.toString());
									return true;
								}
							}
						}
					}
					return false;
				}

			};
			binder.addSetter(imageSetter);
			((T) view).setAdapter(adapter);
		}
	}
}
