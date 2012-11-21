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
package com.momock.outlet.action;

import com.momock.event.Event;
import com.momock.event.IEvent;
import com.momock.event.IEventArgs;
import com.momock.event.IEventHandler;
import com.momock.holder.ImageHolder;
import com.momock.holder.TextHolder;
import com.momock.outlet.IOutlet;
import com.momock.outlet.Plug;

public class ActionPlug extends Plug implements IActionPlug {
	int order = DEFAULT_ORDER;
	TextHolder text = null;
	ImageHolder icon = null;
	IOutlet<IActionPlug, ?> subOutlet = null;
	IEvent<IEventArgs> event = new Event<IEventArgs>();
	
	private ActionPlug()
	{
		
	}
	
	@Override
	public int getOrder() {
		return order;
	}

	@Override
	public TextHolder getText() {
		return text;
	}

	public ActionPlug setText(TextHolder text)
	{
		this.text = text;
		return this;
	}
	
	@Override
	public ImageHolder getIcon() {
		return icon;
	}

	public ActionPlug setIcon(ImageHolder icon)
	{
		this.icon = icon;
		return this;
	}
	
	@Override
	public IOutlet<IActionPlug, ?> getSubOutlet() {
		return subOutlet;
	}

	public ActionPlug setSubOutlet(IOutlet<IActionPlug, ?> subOutlet)
	{
		this.subOutlet = subOutlet;
		return this;
	}
	
	@Override
	public IEvent<IEventArgs> getExecuteEvent() {
		return event;
	}

	public ActionPlug addExecuteEventHandler(IEventHandler<IEventArgs> handler)
	{
		event.addEventHandler(handler);
		return this;
	}
	
	public static ActionPlug get(TextHolder text)
	{
		ActionPlug plug = new ActionPlug();
		return plug.setText(text);
	}
	public static ActionPlug get(TextHolder text, IEventHandler<IEventArgs> handler)
	{
		ActionPlug plug = new ActionPlug();
		return plug.setText(text).addExecuteEventHandler(handler);
	}
	public static ActionPlug get(TextHolder text, ImageHolder icon)
	{
		ActionPlug plug = new ActionPlug();
		return plug.setText(text).setIcon(icon);
	}
	public static ActionPlug get(TextHolder text, ImageHolder icon, IEventHandler<IEventArgs> handler)
	{
		ActionPlug plug = new ActionPlug();
		return plug.setText(text).setIcon(icon).addExecuteEventHandler(handler);
	}
}
