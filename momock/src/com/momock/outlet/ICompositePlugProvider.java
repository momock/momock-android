package com.momock.outlet;

import com.momock.data.IDataList;

public interface ICompositePlugProvider<P extends IPlug>
		extends IPlugProvider<P> {

	void addOutlet(IOutlet<P, ?> outlet);

	void removeOutlet(IOutlet<P, ?> outlet);

	IDataList<IOutlet<P, ?>> getOutlets();	
}
