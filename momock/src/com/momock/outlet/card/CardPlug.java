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
package com.momock.outlet.card;

import com.momock.holder.FragmentHolder;
import com.momock.holder.IComponentHolder;
import com.momock.holder.ViewHolder;
import com.momock.outlet.Plug;

public abstract class CardPlug extends Plug implements ICardPlug{
	public static CardPlug create(final ViewHolder vh){
		return new CardPlug(){

			@Override
			public IComponentHolder getComponent() {
				return vh;
			}
		};
	}
	public static CardPlug create(final FragmentHolder fh){
		return new CardPlug(){

			@Override
			public IComponentHolder getComponent() {
				return fh;
			}
		};
	}
}
