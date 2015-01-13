package com.momock.samples.cases.tab;

import android.view.View;

import com.momock.app.ICase;
import com.momock.binder.ComposedItemBinder;
import com.momock.binder.ItemBinder;
import com.momock.binder.ValueBinderSelector;
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
				ComposedItemBinder cib = new ComposedItemBinder();
				ItemBinder binder1 = new ItemBinder(
						R.layout.samples_list_item,
						new int[] { R.id.sampleItem }, new String[] { "Name" });
				ItemBinder binder2 = new ItemBinder(
						R.layout.samples_list_item2,
						new int[] { R.id.sampleItem }, new String[] { "Name" });
				
				cib.addBinder(new ValueBinderSelector("Type", "L"), binder1);
				cib.addBinder(new ValueBinderSelector("Type", "R"), binder2);
				
				ListViewBinder lvb = new ListViewBinder(cib);
				lvb.bind(ViewHolder.get(view, R.id.lvproducts), ds.getProductsInCategory(category.getId()));
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
