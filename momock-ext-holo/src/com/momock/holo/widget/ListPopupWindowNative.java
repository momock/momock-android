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
package com.momock.holo.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class ListPopupWindowNative extends android.widget.ListPopupWindow implements ListPopupWindow {

	/**
	 * Create a new, empty popup window capable of displaying items from a
	 * ListAdapter. Backgrounds should be set using
	 * {@link #setBackgroundDrawable(Drawable)}.
	 * 
	 * @param context Context used for contained views.
	 */
	public ListPopupWindowNative(final Context context) {
		super(context);
	}

	/**
	 * Create a new, empty popup window capable of displaying items from a
	 * ListAdapter. Backgrounds should be set using
	 * {@link #setBackgroundDrawable(Drawable)}.
	 * 
	 * @param context Context used for contained views.
	 * @param attrs Attributes from inflating parent views used to style the
	 *            popup.
	 */
	public ListPopupWindowNative(final Context context, final AttributeSet attrs) {
		super(context, attrs);
	}

	/**
	 * Create a new, empty popup window capable of displaying items from a
	 * ListAdapter. Backgrounds should be set using
	 * {@link #setBackgroundDrawable(Drawable)}.
	 * 
	 * @param context Context used for contained views.
	 * @param attrs Attributes from inflating parent views used to style the
	 *            popup.
	 * @param defStyleAttr Default style attribute to use for popup content.
	 */
	public ListPopupWindowNative(final Context context, final AttributeSet attrs, final int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	/**
	 * Create a new, empty popup window capable of displaying items from a
	 * ListAdapter. Backgrounds should be set using
	 * {@link #setBackgroundDrawable(Drawable)}.
	 * 
	 * @param context Context used for contained views.
	 * @param attrs Attributes from inflating parent views used to style the
	 *            popup.
	 * @param defStyleAttr Style attribute to read for default styling of popup
	 *            content.
	 * @param defStyleRes Style resource ID to use for default styling of popup
	 *            content.
	 */
	public ListPopupWindowNative(final Context context, final AttributeSet attrs, final int defStyleAttr,
			final int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
	}

}
