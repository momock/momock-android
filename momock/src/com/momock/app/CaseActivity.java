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

import android.app.Activity;
import android.os.Bundle;

public abstract class CaseActivity<T extends ICase> extends Activity {

	protected abstract String getCaseName();
	protected T kase = null;

	@SuppressWarnings("unchecked")
	public T getCase() {
		if (kase == null) {
			kase = (T)App.get().getCase(getCaseName());
		}
		return kase;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getCase().attach(this);
	}

	@Override
	protected void onStart() {
		super.onStart();
		App.get().setActiveCase(getCase());
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		getCase().attach(null);
	}
}
