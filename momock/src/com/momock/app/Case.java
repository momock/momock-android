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


public class Case implements ICase {
	ICase parentCase;
	public Case(ICase parentCase)
	{
		this.parentCase = parentCase;
	}
	public IApplication getApplication()
	{
		return parentCase != null ? parentCase.getApplication() : null;
	}
	public ICase getParent()
	{
		return parentCase;
	}
	@Override
	public void onActivate() {
		
	}
	@Override
	public void onDeactivate() {
		
	}
	@Override
	public void run() {
		
	}

	Object attachedHandle= null;
	@Override
	public Object getAttachedHandle() {
		return attachedHandle;
	}
	@Override
	public void attach(Object target) {
		if (attachedHandle != null)	onDetach();
		attachedHandle = target;	
		if (attachedHandle != null)	onAttach();
	}
	@Override
	public void onAttach()
	{
		
	}
	@Override
	public void onDetach()
	{
		
	}
}
