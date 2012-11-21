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
package com.momock.holder;

import com.momock.app.App;

public class TextHolder implements IGraphicsHolder {
	String text = null;
	public TextHolder(String text){
		this.text = text;
	}

	public TextHolder(int resourceId){
		text = App.get().getResources().getString(resourceId);
	}
	
	public String getText(){
		return text;
	}	
}
