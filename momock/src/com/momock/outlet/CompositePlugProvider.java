/*******************************************************************************
 * Copyright 2012 momock.com
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
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
