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

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

import com.momock.app.ICase;
import com.momock.event.IEventArgs;
import com.momock.event.IEventHandler;
import com.momock.util.Logger;

public abstract class DialogHolder implements IHolder {
	WeakReference<DialogFragment> refDialog = null;

	protected abstract FragmentManager getFragmentManager();

	protected abstract DialogFragment getDialogFragment();

	public void show() {
		DialogFragment df = getDialogFragment();
		FragmentManager fm = getFragmentManager();
		Logger.check(df != null && fm != null, "Fails to open dialog!");
		refDialog = new WeakReference<DialogFragment>(df);
		df.show(fm, "");
	}

	public void close() {
		if (refDialog != null && refDialog.get() != null) {
			refDialog.get().dismiss();
		}
	}

	public static DialogHolder create(final ICase<?> kase, final IHolder title,
			final IHolder message, final TextHolder okButton,
			final IEventHandler<IEventArgs> okHandler) {
		return create(kase, null, title, message, okButton, okHandler, null, null);
	}
	public static DialogHolder create(final ICase<?> kase, final IHolder title,
			final IHolder message, final TextHolder okButton,
			final IEventHandler<IEventArgs> okHandler,
			final TextHolder cancelButton,
			final IEventHandler<IEventArgs> cancelHandler) {
		return create(kase, null, title, message, okButton, okHandler, cancelButton, cancelHandler);
	}
	public static DialogHolder create(final ICase<?> kase, final ImageHolder icon, final IHolder title,
			final IHolder message, final TextHolder okButton,
			final IEventHandler<IEventArgs> okHandler,
			final TextHolder cancelButton,
			final IEventHandler<IEventArgs> cancelHandler) {
		return new DialogHolder() {

			@Override
			public FragmentManager getFragmentManager() {
				if (kase.getAttachedObject() instanceof FragmentActivity)
					return ((FragmentActivity) kase.getAttachedObject())
							.getSupportFragmentManager();
				else if (kase.getAttachedObject() instanceof Fragment)
					return ((Fragment) kase.getAttachedObject())
							.getFragmentManager();
				return null;
			}

			@Override
			protected DialogFragment getDialogFragment() {
				return new DialogFragment() {

					@Override
					public Dialog onCreateDialog(Bundle savedInstanceState) {
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
										okHandler.process(dialog, null);
								}
							};
							builder.setPositiveButton(okButton.getText(), listener);						
						}
						if (cancelButton != null) {
							DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									if (cancelHandler != null)
										cancelHandler.process(dialog, null);
								}
							};
							builder.setNegativeButton(cancelButton.getText(), listener);
						}
						return builder.create();
					}

				};
			}
		};
	}
}
