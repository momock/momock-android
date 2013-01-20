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
package com.momock.outlet.tab;

import com.momock.holder.FragmentHolder;
import com.momock.holder.IComponentHolder;
import com.momock.holder.ImageHolder;
import com.momock.holder.TextHolder;
import com.momock.holder.ViewHolder;
import com.momock.outlet.Plug;

public abstract class TabPlug extends Plug implements ITabPlug {
	int order = DEFAULT_ORDER;

	@Override
	public int getOrder() {
		return order;
	}

	public TabPlug setOrder(int order)
	{
		this.order = order;
		return this;
	}
	
	public static TabPlug create(final TextHolder text, final ImageHolder icon, final ViewHolder content)
	{
		return new TabPlug(){

			@Override
			public TextHolder getText() {
				return text;
			}

			@Override
			public ImageHolder getIcon() {
				return icon;
			}

			@Override
			public IComponentHolder getContent() {
				return content;
			}
			
		};
	}

	public static TabPlug create(final TextHolder text, final ImageHolder icon, final FragmentHolder content)
	{
		return new TabPlug(){

			@Override
			public TextHolder getText() {
				return text;
			}

			@Override
			public ImageHolder getIcon() {
				return icon;
			}

			@Override
			public IComponentHolder getContent() {
				return content;
			}
			
		};
	}
}
