package com.momock.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.SharedPreferences;

import com.momock.event.Event;
import com.momock.event.IEvent;
import com.momock.event.IEventHandler;
import com.momock.util.Convert;

public class Settings implements IDataMutableMap<String, Object>{
	SharedPreferences settings;
	public Settings(Context context, String name){
		this(context, name, Context.MODE_PRIVATE);
	}
	public Settings(Context context, String name, int mode){
		settings = context.getSharedPreferences(name, mode);
	}
	@Override
	public boolean hasProperty(String name) {
		return settings.contains(name);
	}

	@Override
	public Object getProperty(String name) {
		if (hasProperty(name)){
			Map<String, ?> all = settings.getAll();
			return all.get(name);
		}
		return null;
	}
	public String getStringProperty(String name, String def){
		return settings.getString(name, def);
	}
	public int getIntProperty(String name, int def){
		return settings.getInt(name, def);
	}
	public long getLongProperty(String name, long def){
		return settings.getLong(name, def);
	}
	public float getFloatProperty(String name, float def){
		return settings.getFloat(name, def);
	}
	public boolean getBooleanProperty(String name, boolean def){
		return settings.getBoolean(name, def);
	}
	@Override
	public List<String> getPropertyNames() {
		ArrayList<String> names = new ArrayList<String>();
		Map<String, ?> all = settings.getAll();
		names.addAll(all.keySet());
		return names;
	}

	@Override
	public void setProperty(String name, Object val) {
		SharedPreferences.Editor prefEditor = settings.edit();  
		if (val == null)
			prefEditor.remove(name);
		else if (val instanceof CharSequence)
			prefEditor.putString(name, val.toString());
		else if (val instanceof Boolean)
			prefEditor.putBoolean(name, Convert.toBoolean(val));
		else if (val instanceof Double || val instanceof Float)
			prefEditor.putFloat(name, Convert.toDouble(val).floatValue());
		else if (val instanceof Integer)
			prefEditor.putInt(name, Convert.toInteger(val));
		else if (val instanceof Long)
			prefEditor.putLong(name, Convert.toLong(val));
		else
			prefEditor.putString(name, val.toString());
		prefEditor.commit();  
		fireDataChangedEvent();
	}

	@Override
	public void copyPropertiesFrom(IDataMap<String, Object> srouce) {
		beginBatchChange();
		for(String name : srouce.getPropertyNames()){
			setProperty(name, srouce.getProperty(name));
		}
		endBatchChange();
	}
	
	// IDataChangedAware implementation
	IEvent<DataChangedEventArgs> dataChanged = null;
	int batchLevel = 0;
	boolean isDataDirty = false;

	@Override
	public void fireDataChangedEvent() {
		if (batchLevel > 0){
			isDataDirty = true;
		} else {
			if (dataChanged != null)
				dataChanged.fireEvent(this, new DataChangedEventArgs());
		}
	}

	@Override
	public void addDataChangedHandler(
			IEventHandler<DataChangedEventArgs> handler) {
		if (dataChanged == null)
			dataChanged = new Event<DataChangedEventArgs>();
		dataChanged.addEventHandler(handler);
	}

	@Override
	public void removeDataChangedHandler(
			IEventHandler<DataChangedEventArgs> handler) {
		if (dataChanged == null)
			return;
		dataChanged.removeEventHandler(handler);
	}

	@Override
	public void beginBatchChange() {
		if (batchLevel == 0)
			isDataDirty = false;
		batchLevel ++;	
	}

	@Override
	public void endBatchChange() {
		batchLevel --;
		if (batchLevel == 0 && isDataDirty)
			fireDataChangedEvent();
	}
}
