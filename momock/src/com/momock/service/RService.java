/*******************************************************************************
 * Copyright 2013 momock.com
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
package com.momock.service;

import javax.inject.Inject;

import android.content.Context;
import android.content.res.Resources;

public class RService implements IRService{
	@Inject
	Resources resources;
	@Inject
	Context context;
	@Override
	public Class<?>[] getDependencyServices() {
		return null;
	}

	@Override
	public void start() {
		
	}

	@Override
	public void stop() {
		
	}

	@Override
	public boolean canStop() {
		return true;
	}

	@Override
	public Integer getAnim(String name) {
		return getAnim(name, null);
	}

	@Override
	public Integer getAnim(String name, Integer defaultVal) {
		return get("anim", name, defaultVal);
	}

	@Override
	public Integer getAttr(String name) {
		return getAttr(name, null);
	}

	@Override
	public Integer getAttr(String name, Integer defaultVal) {
		return get("attr", name, defaultVal);
	}

	@Override
	public Integer getColor(String name) {
		return getColor(name, null);
	}

	@Override
	public Integer getColor(String name, Integer defaultVal) {
		return get("color", name, defaultVal);
	}

	@Override
	public Integer getDimen(String name) {
		return getDimen(name, null);
	}

	@Override
	public Integer getDimen(String name, Integer defaultVal) {
		return get("dimen", name, defaultVal);
	}

	@Override
	public Integer getDrawable(String name) {
		return getDrawable(name, null);
	}

	@Override
	public Integer getDrawable(String name, Integer defaultVal) {
		return get("drawable", name, defaultVal);
	}

	@Override
	public Integer getId(String name) {
		return getId(name, null);
	}

	@Override
	public Integer getId(String name, Integer defaultVal) {
		return get("id", name, defaultVal);
	}

	@Override
	public Integer getLayout(String name) {
		return getLayout(name, null);
	}

	@Override
	public Integer getLayout(String name, Integer defaultVal) {
		return get("layout", name, defaultVal);
	}

	@Override
	public Integer getString(String name) {
		return getString(name, null);
	}

	@Override
	public Integer getString(String name, Integer defaultVal) {
		return get("string", name, defaultVal);
	}

	@Override
	public Integer getStyle(String name) {
		return getStyle(name, null);
	}

	@Override
	public Integer getStyle(String name, Integer defaultVal) {
		return get("style", name, defaultVal);
	}

	@Override
	public Integer get(String type, String name) {
		return get(type, name, null);
	}

	@Override
	public Integer get(String type, String name, Integer defaultVal) {
		int id = resources.getIdentifier(name, type, context.getPackageName());
		return id == 0 ? defaultVal : id;
	}

}
