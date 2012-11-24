package com.momock.outlet;

import com.momock.data.DataList;
import com.momock.data.IDataList;

// TODO : make it data aware
public class CompositePlugProvider<P extends IPlug> implements ICompositePlugProvider<P>{
	DataList<IOutlet<P, ?>> outlets = new DataList<IOutlet<P, ?>>();
	DataList<P> plugs = new DataList<P>();
	
	@Override
	public IDataList<P> getPlugs() {
		return plugs;
	}

	@Override
	public void addOutlet(IOutlet<P, ?> outlet) {
		if (!outlets.hasItem(outlet))
			outlets.addItem(outlet);
	}

	@Override
	public void removeOutlet(IOutlet<P, ?> outlet) {
		if (outlets.hasItem(outlet))
			outlets.addItem(outlet);
	}

	@Override
	public IDataList<IOutlet<P, ?>> getOutlets() {
		return outlets;
	}

}
