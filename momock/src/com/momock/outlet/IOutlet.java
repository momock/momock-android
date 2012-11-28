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

public interface IOutlet<P extends IPlug, T> extends IPlugProvider<P> {
	IPlugProvider<P> getPlugProvider();

	void setPlugProvider(IPlugProvider<P> provider);

	P addPlug(P plug);

	void removePlug(P plug);

	T getAttachedObject();

	void attach(T target);

	void detach();

	void onAttach(T target);

	void onDetach(T target);

	P getActivePlug();

	void setActivePlug(P plug);

	void onActivate(P plug);

	void onDeactivate(P plug);
	
	int getIndexOf(P plug);
}
