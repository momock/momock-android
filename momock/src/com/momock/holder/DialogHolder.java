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

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.momock.event.EventArgs;
import com.momock.event.IEventHandler;
import com.momock.util.Logger;

public class DialogHolder implements IHolder {
	protected ImageHolder icon;
	protected IHolder title;
	protected IHolder message;
	protected TextHolder okButton;
	protected IEventHandler<EventArgs> okHandler;
	protected TextHolder cancelButton;
	protected IEventHandler<EventArgs> cancelHandler;
	protected boolean cancelable = true;
	protected DialogHolder(ImageHolder icon, IHolder title,
			IHolder message, TextHolder okButton,
			IEventHandler<EventArgs> okHandler,
			TextHolder cancelButton,
			IEventHandler<EventArgs> cancelHandler,
			boolean cancelable){
		this.icon = icon;
		this.title = title;
		this.message = message;
		this.okButton = okButton;
		this.okHandler = okHandler;
		this.cancelButton = cancelButton;
		this.cancelHandler = cancelHandler;
		this.cancelable = cancelable;
	}
	public void show(Context context) {
		try{			
			AlertDialog.Builder builder = new AlertDialog.Builder(context);
			if (icon != null){
				builder.setIcon(icon.getAsDrawable());
			}
			if (message != null) {
				Logger.check(message instanceof TextHolder
						|| message instanceof ViewHolder,
						"message must be either a TextHolder or a ViewHolder");
				if (message instanceof TextHolder)
					builder.setMessage(((TextHolder) message)
							.getText());
				else{
					((ViewHolder)message).reset();
					builder.setView(((ViewHolder)message).getView());
				}
			}
			if (title != null) {
				Logger.check(title instanceof TextHolder
						|| title instanceof ViewHolder,
						"title must be either a TextHolder or a ResourceHolder");
				if (title instanceof TextHolder)
					builder.setTitle(((TextHolder) title).getText());
				else{
					((ViewHolder)title).reset();
					builder.setCustomTitle(((ViewHolder)title).getView());
				}
			}
			if (okButton != null) {
				DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,
							int id) {
						if (okHandler != null)
							okHandler.process(this, null);
						dialog.dismiss();
					}
				};
				builder.setPositiveButton(okButton.getText(), listener);						
			}
			if (cancelButton != null) {
				DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,
							int id) {
						if (cancelHandler != null)
							cancelHandler.process(this, null);
						dialog.dismiss();
					}
				};
				builder.setNegativeButton(cancelButton.getText(), listener);
			}
			builder.setCancelable(cancelable).create().show();
		}catch(Exception e){
			Logger.error(e);
		}
	}

	public static DialogHolder create(IHolder title,
			IHolder message, TextHolder okButton,
			IEventHandler<EventArgs> okHandler) {
		return create(title, message, okButton, okHandler, null, null);
	}
	public static DialogHolder create(IHolder title,
			IHolder message, TextHolder okButton,
			IEventHandler<EventArgs> okHandler,
			TextHolder cancelButton,
			IEventHandler<EventArgs> cancelHandler) {
		return create(null, title, message, okButton, okHandler, cancelButton, cancelHandler);
	}
	public static DialogHolder create(ImageHolder icon, IHolder title,
			IHolder message, TextHolder okButton,
			IEventHandler<EventArgs> okHandler,
			TextHolder cancelButton,
			IEventHandler<EventArgs> cancelHandler) {		
		return create(icon, title, message, okButton, okHandler, cancelButton, cancelHandler, false);
	}
	public static DialogHolder create(ImageHolder icon, IHolder title,
			IHolder message, TextHolder okButton,
			IEventHandler<EventArgs> okHandler,
			TextHolder cancelButton,
			IEventHandler<EventArgs> cancelHandler,
			boolean cancelable) {		
		return new DialogHolder(icon, title, message, okButton, okHandler, cancelButton, cancelHandler, cancelable);
	}
}
