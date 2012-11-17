package com.momock.outlet.action;

import com.momock.event.Event;
import com.momock.event.IEvent;
import com.momock.event.IEventArgs;
import com.momock.event.IEventHandler;
import com.momock.holder.ImageHolder;
import com.momock.holder.TextHolder;
import com.momock.outlet.IOutlet;

public class ActionPlug implements IActionPlug {
	int order = DEFAULT_ORDER;
	TextHolder text = null;
	ImageHolder icon = null;
	IOutlet<IActionPlug> subOutlet = null;
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
	public IOutlet<IActionPlug> getSubOutlet() {
		return subOutlet;
	}

	public ActionPlug setSubOutlet(IOutlet<IActionPlug> subOutlet)
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
