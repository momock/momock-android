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

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;

import com.momock.app.IApplication;
import com.momock.app.ICase;
import com.momock.event.EventArgs;
import com.momock.event.IEventHandler;
import com.momock.util.Logger;

public abstract class DialogHolder implements IHolder {
	public static class SimpleDialogFragment extends DialogFragment{
		public static final String INDEX = "INDEX";		
		public SimpleDialogFragment(){
			
		}
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			int index = getArguments().getInt(INDEX);
			Logger.check(index >= 0 && index < dialogData.size(), "Parameter error !");
			DialogData dd = dialogData.get(index);
			ImageHolder icon = dd.icon;
			IHolder title = dd.title;
			IHolder message = dd.message;
			TextHolder okButton = dd.okButton;
			final IEventHandler<EventArgs> okHandler = dd.okHandler;
			TextHolder cancelButton = dd.cancelButton;
			final IEventHandler<EventArgs> cancelHandler = dd.cancelHandler;
			
			AlertDialog.Builder builder = new AlertDialog.Builder(
					getActivity());
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
				else
					builder.setView(((ViewHolder)message).getView());
			}
			if (title != null) {
				Logger.check(title instanceof TextHolder
						|| title instanceof ViewHolder,
						"title must be either a TextHolder or a ResourceHolder");
				if (title instanceof TextHolder)
					builder.setTitle(((TextHolder) title).getText());
				else
					builder.setCustomTitle(((ViewHolder)title).getView());
			}
			if (okButton != null) {
				DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,
							int id) {
						if (okHandler != null)
							okHandler.process(this, null);
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
					}
				};
				builder.setNegativeButton(cancelButton.getText(), listener);
			}
			return builder.create();
		}
	}
	static class DialogData{
		public ImageHolder icon;
		public IHolder title;
		public IHolder message;
		public TextHolder okButton;
		public IEventHandler<EventArgs> okHandler;
		public TextHolder cancelButton;
		public IEventHandler<EventArgs> cancelHandler;
	}
	static List<DialogData> dialogData = new ArrayList<DialogData>();
	public static void onStaticCreate(IApplication app){
		
	}
	public static void onStaticDestroy(IApplication app){
		dialogData.clear();
	}
	WeakReference<DialogFragment> refDialog = null;

	protected abstract FragmentManager getFragmentManager();

	protected abstract DialogFragment getDialogFragment();

	public void show() {
		try{
			DialogFragment df = getDialogFragment();
			FragmentManager fm = getFragmentManager();
			Logger.check(df != null && fm != null, "Fails to open dialog!");
			refDialog = new WeakReference<DialogFragment>(df);
			df.show(fm, "");
		}catch(Exception e){
			Logger.error(e);
		}
	}

	public void close() {
		if (refDialog != null && refDialog.get() != null) {
			refDialog.get().dismiss();
		}
	}

	public static DialogHolder create(final ICase<?> kase, final IHolder title,
			final IHolder message, final TextHolder okButton,
			final IEventHandler<EventArgs> okHandler) {
		return create(kase, null, title, message, okButton, okHandler, null, null);
	}
	public static DialogHolder create(final ICase<?> kase, final IHolder title,
			final IHolder message, final TextHolder okButton,
			final IEventHandler<EventArgs> okHandler,
			final TextHolder cancelButton,
			final IEventHandler<EventArgs> cancelHandler) {
		return create(kase, null, title, message, okButton, okHandler, cancelButton, cancelHandler);
	}
	public static DialogHolder create(final ICase<?> kase, final ImageHolder icon, final IHolder title,
			final IHolder message, final TextHolder okButton,
			final IEventHandler<EventArgs> okHandler,
			final TextHolder cancelButton,
			final IEventHandler<EventArgs> cancelHandler) {
		return create(FragmentManagerHolder.get(kase), icon, title, message, okButton, okHandler, cancelButton, cancelHandler);
	}
	public static DialogHolder create(final FragmentManagerHolder fmh,
			ImageHolder icon, IHolder title,
			IHolder message, TextHolder okButton,
			IEventHandler<EventArgs> okHandler,
			TextHolder cancelButton,
			IEventHandler<EventArgs> cancelHandler) {
		final int index = dialogData.size();
		Logger.debug("DialogHolder #" + index);
		DialogData dd = new DialogData();
		dd.icon = icon;
		dd.title = title;
		dd.message = message;
		dd.okButton = okButton;
		dd.okHandler = okHandler;
		dd.cancelButton = cancelButton;
		dd.cancelHandler = cancelHandler;
		dialogData.add(dd);
		
		return new DialogHolder() {

			@Override
			public FragmentManager getFragmentManager() {
				return fmh.getFragmentManager();
			}

			@Override
			protected DialogFragment getDialogFragment() {
				Bundle args = new Bundle();
				args.putInt(SimpleDialogFragment.INDEX, index);
				SimpleDialogFragment fragment = new SimpleDialogFragment();
				fragment.setArguments(args);
				return fragment;
			}
		};
	}
}
