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

public abstract class DialogHolder implements IHolder{
	WeakReference<DialogFragment> refDialog = null;
	protected abstract FragmentManager getFragmentManager();
	protected abstract DialogFragment getDialogFragment();
	public void show(){
		DialogFragment df = getDialogFragment();
		FragmentManager fm = getFragmentManager();
		Logger.check(df != null && fm != null, "Fails to open dialog!");
		refDialog = new WeakReference<DialogFragment>(df);
		df.show(fm, "");
	}
	public void close(){
		if (refDialog != null && refDialog.get() != null){
			refDialog.get().dismiss();
		}
	}
	public static DialogHolder create(final ICase<?> kase, final TextHolder title, final TextHolder message, 
			final TextHolder okButton, final IEventHandler<IEventArgs> okHandler){
		return create(kase, title, message, okButton, okHandler, null, null);
	}
	public static DialogHolder create(final ICase<?> kase, final TextHolder title, final TextHolder message, 
			final TextHolder okButton, final IEventHandler<IEventArgs> okHandler, final TextHolder cancelButton, final IEventHandler<IEventArgs> cancelHandler){
		return new DialogHolder(){

			@Override
			public FragmentManager getFragmentManager() {
				if (kase.getAttachedObject() instanceof FragmentActivity)
					return ((FragmentActivity)kase.getAttachedObject()).getSupportFragmentManager();
				else if (kase.getAttachedObject() instanceof Fragment)
					return ((Fragment)kase.getAttachedObject()).getFragmentManager();
				return null;
			}

			@Override
			protected DialogFragment getDialogFragment() {
				return new DialogFragment(){

					@Override
					public Dialog onCreateDialog(Bundle savedInstanceState) {
						AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
						if (message != null)
							builder.setMessage(message.getText());
						if (title != null)
							builder.setTitle(title.getText());
						if (okButton != null && okHandler != null)
							builder.setPositiveButton(okButton.getText(), new DialogInterface.OnClickListener() {
				                   public void onClick(DialogInterface dialog, int id) {
				                	   okHandler.process(dialog, null);
				                   }
				               });
						if (cancelButton != null && cancelHandler != null)
							builder.setNegativeButton(cancelButton.getText(), new DialogInterface.OnClickListener() {
				                   public void onClick(DialogInterface dialog, int id) {
				                	   cancelHandler.process(dialog, null);
				                   }
				               });
						return builder.create();
					}
					
				};
			}
		};
	}
}