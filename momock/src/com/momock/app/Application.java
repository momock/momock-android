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

import com.momock.util.Logger;

public class Application extends android.app.Application implements IApplication{
	static IApplication app = null;
	public static IApplication getInstance()
	{
		return app;
	}
	protected int getLogLevel()
	{
		return Logger.LEVEL_DEBUG;
	}
	@Override
	public void onCreate() {
		Logger.open(this.getClass().getName().toLowerCase() + ".log", getLogLevel());
		app = this;
		super.onCreate();
	}

	@Override
	public void onTerminate() {
		super.onTerminate();
		Logger.close();
	}

}
