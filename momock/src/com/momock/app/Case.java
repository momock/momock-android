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
	String name;
	ICase parentCase;
	Object attachedObject = null;
	public Case(ICase parentCase)
	{
		this.parentCase = parentCase;
		this.name = this.getClass().getName();
	}
	public Case(ICase parentCase, String name)
	{
		this.parentCase = parentCase;
		this.name = name;
	}
	public IApplication getApplication()
	{
		return parentCase != null ? parentCase.getApplication() : null;
	}
	public ICase getParent()
	{
		return parentCase;
	}
	public String getName()
	{
		return name;
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
	@Override
	public Object getAttachedObject() {
		return attachedObject;
	}
	@Override
	public void setAttachedObject(Object target) {
		if (attachedObject != null)	onDetach();
		attachedObject = target;	
		if (attachedObject != null)	onAttach();
	}
	protected void onAttach()
	{
		
	}
	protected void onDetach()
	{
		
	}
}
