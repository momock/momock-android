package com.momock.outlet;

import java.util.ArrayList;
import java.util.List;

public class PlaceholderOutlet implements IOutlet<IPlug>{
	List<IPlug> plugs = new ArrayList<IPlug>();
	@Override
	public IPlug addPlug(IPlug plug) {
		plugs.add(plug);
		return plug;
	}

	@Override
	public void removePlug(IPlug plug) {
		plugs.remove(plug);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void transfer(IOutlet outlet)
	{
		for(int i = 0; i < plugs.size(); i++)
			outlet.addPlug(plugs.get(i));
	}
}
