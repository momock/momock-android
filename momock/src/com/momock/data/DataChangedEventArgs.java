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
package com.momock.data;

import com.momock.event.EventArgs;

public class DataChangedEventArgs extends EventArgs{
	public static final int CAUSE_UNSPECIFIC = 0;
	public static final int CAUSE_ADD_ITEM = 1;
	public static final int CAUSE_REMOVE_ITEM = 2;
	public static final int CAUSE_CHANGE_PROPERTY = 3;
	int cause = CAUSE_UNSPECIFIC;
	Object data = null;
	public DataChangedEventArgs()
	{
		
	}
	public DataChangedEventArgs(int cause, Object data)
	{
		this.cause = cause;
		this.data = data;
	}
	public int getCause()
	{
		return cause;
	}
	public Object getData()
	{
		return data;
	}
}
