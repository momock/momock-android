package com.momock.outlet;

import com.momock.data.IDataList;

public interface IPlugProvider<P extends IPlug> {
	IDataList<P> getPlugs();
}
