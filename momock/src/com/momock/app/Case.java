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

import java.util.HashMap;
import java.util.Map;

import com.momock.data.IDataSet;
import com.momock.message.IMessageHandler;
import com.momock.message.Message;
import com.momock.outlet.IOutlet;
import com.momock.outlet.IPlug;
import com.momock.outlet.PlaceholderOutlet;
import com.momock.service.IService;
import com.momock.util.Logger;


public abstract class Case<A> implements ICase<A> {
	String name;
	public Case(){
		name = getClass().getName();
	}
	public Case(String name)
	{
		this.name = name;
	}
	public Case(ICase<?> parent){
		this.parent = parent;
		this.name = getClass().getName();
	}

	public Case(ICase<?> parent, String name){
		this.parent = parent;
		this.name = name;
	}
	public String getName(){
		return name;
	}
	
	public String getFullName(){
		return getParent() != null ? getParent().getFullName() + "/" + name : "/" + name;
	}
	
	public abstract void onCreate();
	
	@Override
	public void onActivate() {
		
	}
	@Override
	public void onDeactivate() {
		
	}
	@Override
	public void run(Object... args) {
		
	}

	// IAttachable implementation
	A attachedObject = null;
	@Override
	public A getAttachedObject() {
		return attachedObject;
	}

	@Override
	public void attach(final A target) {
		if (attachedObject != null){
			Logger.debug("onDetach " + getFullName() + "(" + Integer.toHexString(this.hashCode()) +") : " + attachedObject + "(" + Integer.toHexString(attachedObject.hashCode()) +")");
		}
		detach();
		if (target != null)
		{	
			Logger.debug("onAttach " + getFullName() + "(" + Integer.toHexString(this.hashCode()) +") : " + target + "(" + Integer.toHexString(target.hashCode()) +")");
			attachedObject = target;
			onAttach(target);	
		}
	}
	
	@Override
	public void detach(){
		if (getAttachedObject() != null)
		{
			onDetach(attachedObject);
			attachedObject = null;
		}
	}
	
	@Override
	public void onAttach(A target)
	{
	}
	@Override
	public void onDetach(A target)
	{
	}

	// Implementation for ICase interface
	protected ICase<?> activeCase = null;
	protected ICase<?> parent;
	protected HashMap<String, ICase<?>> cases = new HashMap<String, ICase<?>>();

	@Override
	public ICase<?> getParent() {
		return parent;
	}

	@Override
	public boolean isActive(){
		return this == (getParent() == null ? App.get().getActiveCase() : getParent().getActiveCase());
	}
	
	@Override
	public ICase<?> getActiveCase() {
		return activeCase;
	}

	@Override
	public void setActiveCase(ICase<?> kase) {
		if (activeCase != kase) {
			if (activeCase != null)
				activeCase.onDeactivate();
			activeCase = kase;
			if (activeCase != null)
				activeCase.onActivate();
		}
	}
	
	@Override
	public ICase<?> getCase(String name) {
		Logger.check(name != null, "Parameter name cannot be null!");
		ICase<?> kase = null;
		int pos = name.indexOf('/');
		if (pos == -1){
			kase = cases.get(name);	
			if (kase == null)
				return getParent() == null ? App.get().getCase(name) : getParent().getCase(name);
		} else {
			if (name.startsWith("/"))
				kase = App.get().getCase(name);
			else{
				kase = cases.get(name.substring(0, pos));
				if (kase != null)
					kase = kase.getCase(name.substring(pos + 1));
			}
		}
		return kase;
	}

	@Override
	public ICase<?> findChildCase(String name){
		ICase<?> kase = getCase(name);
		if (kase == null){
			for(Map.Entry<String, ICase<?>> e : cases.entrySet()){
				kase = e.getValue().findChildCase(name);
				if (kase != null) return kase;
			}
		}
		return kase;
	}
	@Override
	public void addCase(ICase<?> kase){
		if (!cases.containsKey(kase.getName()))
		{
			cases.put(kase.getName(), kase);
			kase.onCreate();
		}
	}
	
	@Override
	public void removeCase(String name) {
		if (cases.containsKey(name))
			cases.remove(name);
	}

	@SuppressWarnings("rawtypes")
	HashMap<String, IOutlet> outlets = new HashMap<String, IOutlet>(); 

	@SuppressWarnings({"rawtypes" })
	@Override
	public IOutlet getOutlet(String name) {
		IOutlet outlet = null;
		if (outlets.containsKey(name))
			outlet = outlets.get(name);
		if (outlet == null)
			outlet = (getParent() == null ? App.get().getOutlet(name) : getParent().getOutlet(name));
		if (outlet == null)
		{
			outlet = new PlaceholderOutlet();
			outlets.put(name, outlet);
		}
		return outlet;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void addOutlet(String name, IOutlet outlet) {
		Logger.debug("addOutlet : " + name);
		if (outlets.containsKey(name) && outlet != null)
		{
			IOutlet<?, ?> oldOutlet = outlets.get(name);
			if (oldOutlet instanceof PlaceholderOutlet)
				((PlaceholderOutlet<?, ?>)oldOutlet).transfer(outlet);
		}
		if (outlet == null)
			outlets.remove(name);
		else
			outlets.put(name, outlet);
	}

	@Override
	public void removeOutlet(String name) {
		if (outlets.containsKey(name))
		{
			outlets.remove(name);
		}
	}
	
	IDataSet ds = null;
	@Override
	public IDataSet getDataSet() {
		if (ds == null)
			ds = new CaseDataSet(this);
		return ds;
	}
	
	Map<String, IPlug> plugs = new HashMap<String, IPlug>();
	@Override
	public void addPlug(String name, IPlug plug) {
		plugs.put(name, plug);
	}

	@Override
	public IPlug getPlug(String name) {
		return plugs.get(name);
	}

	@Override
	public void removePlug(String name){
		plugs.remove(name);
	}

	Map<Class<?>, IService> services = new HashMap<Class<?>, IService>();
	@SuppressWarnings("unchecked")
	@Override
	public <T extends IService> T getService(Class<?> klass) {
		if (services.containsKey(klass))
			return (T) services.get(klass);
		return (T) (getParent() == null ? App.get().getService(klass) : getParent().getService(klass));
	}
	@Override
	public void addService(Class<?> klass, IService service) {
		services.put(klass, service);
	}
	@Override
	public boolean onBack() {
		if (getActiveCase() != null)
			return getActiveCase().onBack();
		return false;
	}
	@Override
	public void onShow() {
		
	}
	@Override
	public void onHide() {
		
	}
	@Override
	public void sendMessage(Object sender, String topic) {
		App.get().getMessageBox().send(sender, topic);
	}
	@Override
	public void sendMessage(Object sender, String topic, Object data) {
		App.get().getMessageBox().send(sender, topic, data);
	}
	@Override
	public void sendMessage(Object sender, Message msg) {
		App.get().getMessageBox().send(sender, msg);
	}
	@Override
	public void addMessageHandler(String topic, IMessageHandler handler) {
		App.get().getMessageBox().addHandler(topic, handler);
	}
	@Override
	public void removeMessageHandler(String topic, IMessageHandler handler) {
		App.get().getMessageBox().removeHandler(topic, handler);		
	}
	@Override
	public boolean isAttached() {
		return getAttachedObject() != null;
	}
}
