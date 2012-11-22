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
package com.momock.app;

import com.momock.outlet.IOutlet;
import com.momock.outlet.IPlug;

public interface IApplication {
	ICase<?> getActiveCase();

	void setActiveCase(ICase<?> kase);

	ICase<?> getCase(String name);

	void addCase(ICase<?> kase);

	void removeCase(String name);

	<P extends IPlug, T> IOutlet<P, T> getOutlet(String name);

	<P extends IPlug, T> void addOutlet(String name, IOutlet<P, T> outlet);

	void removeOutlet(String name);
	
	void addNamedPlug(String name, IPlug plug);
	
	IPlug getNamedPlug(String name);
}
