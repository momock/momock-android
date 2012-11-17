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
