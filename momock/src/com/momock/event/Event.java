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
package com.momock.event;

import java.util.ArrayList;
import java.util.List;

public class Event<A extends IEventArgs> implements IEvent<A> {
	// In most cases, there is only one event handler. To reduce memory usage, handlers will refer to the handler directly 
	Object handlers = null;
	
	@SuppressWarnings("unchecked")
	@Override
	public void fireEvent(Object sender, A args) {
		if (handlers == null) return;
		if (handlers instanceof IEventHandler<?>) {
			((IEventHandler<A>)handlers).process(sender, args);
		} else {
			for(IEventHandler<A> handler : (List<IEventHandler<A>>)handlers)
			{
				handler.process(sender, args);
			}			
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void addEventHandler(IEventHandler<A> handler) {
		if (handlers == null) 
			handlers = handler;
		else if (handlers instanceof IEventHandler<?>)
		{
			List<IEventHandler<A>> hs = new ArrayList<IEventHandler<A>>();
			hs.add((IEventHandler<A>)handlers);
			hs.add(handler);
			handlers = hs;
		}
		else
		{
			if (!((List<IEventHandler<A>>)handlers).contains(handler))
				((List<IEventHandler<A>>)handlers).add(handler);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void removeEventHandler(IEventHandler<A> handler) {
		if (handlers == null)
			return;
		else if (handlers instanceof IEventHandler<?>)
		{
			if (handlers == handler)
				handlers = null;
		}
		else
		{
			List<IEventHandler<A>> hs = (List<IEventHandler<A>>)handlers;
			hs.remove(handler);
			if (hs.size() ==0)
				handlers = null;
			else if (hs.size() == 1)
				handlers = hs.get(0);						
		}	
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean hasEventHandler(IEventHandler<A> handler) {
		if (handlers == null)
			return false;
		else if (handlers instanceof IEventHandler<?>)
		{
			return handlers == handler;
		}
		else
		{
			List<IEventHandler<A>> hs = (List<IEventHandler<A>>)handlers;
			return hs.contains(handler);
		}	
	}
	
}
