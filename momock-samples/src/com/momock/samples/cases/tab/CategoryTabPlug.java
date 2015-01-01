package com.momock.samples.cases.tab;

import android.view.View;

import com.momock.app.ICase;
import com.momock.binder.container.ListViewBinder;
import com.momock.holder.IComponentHolder;
import com.momock.holder.ImageHolder;
import com.momock.holder.TextHolder;
import com.momock.holder.ViewHolder;
import com.momock.holder.ViewHolder.OnViewCreatedHandler;
import com.momock.outlet.tab.TabPlug;
import com.momock.samples.R;
import com.momock.samples.model.Category;
import com.momock.samples.services.IDataService;


public class CategoryTabPlug extends TabPlug {
	Category category;
	TextHolder title = new TextHolder(){

		@Override
		public String getText() {
			return category.getName();
		}
		
	};
	ViewHolder content;
	public CategoryTabPlug(final ICase<?> kase, Category cat){
		category = cat;
		content = ViewHolder.create(kase, R.layout.case_listview, new OnViewCreatedHandler(){

			@Override
			public void onViewCreated(View view) {
				IDataService ds = kase.getService(IDataService.class);
				ListViewBinder binder = ListViewBinder.getSimple("Name");
				binder.bind(ViewHolder.get(view, R.id.lvproducts), ds.getProductsInCategory(category.getId()));
			}
			
		});

	}
	@Override
	public TextHolder getText() {
		return title;
	}

	@Override
	public ImageHolder getIcon() {
		return null;
	}

	@Override
	public IComponentHolder getContent() {
		return content;
	}

	public Category getCategory() {
		return category;
	}
}
