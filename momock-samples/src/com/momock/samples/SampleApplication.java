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
import com.momock.samples.cases.holo.HoloCase;
import com.momock.samples.cases.main.MainCase;
import com.momock.samples.cases.settings.SettingsCase;
import com.momock.samples.services.IDataService;
import com.momock.samples.services.XmlDataService;
import com.momock.service.CacheService;
import com.momock.service.HttpService;
import com.momock.service.ICacheService;
import com.momock.service.IHttpService;
import com.momock.service.IImageService;
import com.momock.service.ImageService;
import com.momock.util.Logger;

public class SampleApplication extends App{

	@Override
	public void onCreate() {
		super.onCreate();
		Logger.debug("SampleApplication.onCreate");
	}
	
	@Override
	protected void onAddCases() {
		addCase(new MainCase(CaseNames.MAIN));
		addCase(new HoloCase(CaseNames.HOLO));
		addCase(new SettingsCase(CaseNames.SETTINGS));
	}

	@Override
	protected void onAddServices() {
		addService(IHttpService.class, new HttpService()); 
		addService(ICacheService.class, new CacheService(this));
		addService(IImageService.class, new ImageService());
		addService(IDataService.class, new XmlDataService());
	}

	@Override
	public void onCreateLog(LogConfig config) {
		config.level = Logger.LEVEL_DEBUG;
		config.name = "momock-samples";
	}


}
