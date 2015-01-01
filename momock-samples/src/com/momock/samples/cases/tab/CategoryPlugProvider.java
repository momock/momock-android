package com.momock.samples.cases.tab;

import java.util.ArrayList;
import java.util.List;

import com.momock.app.ICase;
import com.momock.data.DataChangedEventArgs;
import com.momock.data.IDataList;
import com.momock.data.IDataMutableList;
import com.momock.event.IEventHandler;
import com.momock.outlet.IPlug;
import com.momock.outlet.IPlugProvider;
import com.momock.samples.model.Category;

public class CategoryPlugProvider implements IPlugProvider {
	List<IPlug> peerPlugs = new ArrayList<IPlug>();
	IDataList<IPlug> plugs = new IDataList<IPlug>(){
		
		@Override
		public boolean hasItem(IPlug item) {			
			return peerPlugs.contains(item);
		}

		@Override
		public IPlug getItem(int index) {
			return peerPlugs.get(index);
		}

		@Override
		public int getItemCount() {
			return peerPlugs.size();
		}

	};
	@Override
	public IDataList<IPlug> getPlugs() {
		return plugs;
	}
	IDataMutableList<Category> dataSource;
	ICase<?> kase;
	public CategoryPlugProvider(ICase<?> kase, IDataMutableList<Category> cats){
		this.kase = kase;
		dataSource = cats;
		dataSource.addDataChangedHandler(new IEventHandler<DataChangedEventArgs>(){

			@Override
			public void process(Object sender, DataChangedEventArgs args) {
				refreshPlugs();				
			}
			
		});
		refreshPlugs();
	}
	void refreshPlugs(){
		peerPlugs.clear();
		for(int i = 0; i < dataSource.getItemCount(); i++){
			Category cat = dataSource.getItem(i);
			peerPlugs.add(new CategoryTabPlug(kase, cat));
		}
	}
}
