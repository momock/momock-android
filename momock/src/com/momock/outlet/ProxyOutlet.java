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

public class ProxyOutlet implements IOutlet<IPlug>{
	DataList<IOutlet<?>> outlets = new DataList<IOutlet<?>>();

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public IPlug addPlug(IPlug plug) {
		for(int i = 0; i < outlets.getItemCount(); i ++)
		{
			IOutlet outlet = outlets.getItem(i);
			outlet.addPlug(plug);
		}
		return plug;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void removePlug(IPlug plug) {
		for(int i = 0; i < outlets.getItemCount(); i ++)
		{
			IOutlet outlet = outlets.getItem(i);
			outlet.removePlug(plug);
		}
	}
	
	public void addOutlet(IOutlet<?> outlet)
	{
		outlets.addItem(outlet);
	}
	public void removeOutlet(IOutlet<?> outlet)
	{
		outlets.removeItem(outlet);
	}
}
