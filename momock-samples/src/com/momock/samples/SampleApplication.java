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
package com.momock.samples;

import com.momock.app.App;
import com.momock.samples.cases.action.ActionCase;
import com.momock.util.Logger;

public class SampleApplication extends App{

	@Override
	public void onCreate() {
		super.onCreate();
		Logger.debug("SampleApplication.onCreate");
	}
	
	@Override
	protected void onAddCases() {
		addCase(Cases.MAIN, new MainCase());
		addCase(Cases.SAMPLE_ACTION, new ActionCase());
	}

	@Override
	protected void onAddServices() {
	}

}
