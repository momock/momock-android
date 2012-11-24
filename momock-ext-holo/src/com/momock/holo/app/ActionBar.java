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
package com.momock.holo.app;

import android.graphics.drawable.Drawable;
import android.view.View;

public interface ActionBar {

	public View getCustomView();

	public int getHeight();

	public CharSequence getSubtitle();

	public CharSequence getTitle();

	public void setBackgroundDrawable(Drawable paramDrawable);

	public void setCustomView(int resId);

	public void setCustomView(View view);

	public void setDisplayHomeAsUpEnabled(boolean showHomeAsUp);

	public void setDisplayShowCustomEnabled(boolean showCustom);

	public void setDisplayShowHomeEnabled(boolean sShowHome);

	public void setDisplayShowTitleEnabled(boolean showTitle);

	public void setSubtitle(CharSequence subtitle);

	public void setSubtitle(int resId);

	public void setTitle(CharSequence title);

	public void setTitle(int resId);

}
