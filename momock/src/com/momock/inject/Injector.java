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
package com.momock.inject;

import java.lang.reflect.Field;
import java.util.HashMap;

import javax.inject.Provider;

import com.momock.util.Logger;

public class Injector {
	HashMap<Class<?>, Provider<?>> providers = new HashMap<Class<?>, Provider<?>>();

	public Injector() {
	}

	public void addProvider(Class<?> klass, Provider<?> provider) {
		providers.put(klass, provider);
	}

	public void addProvider(Class<?> klass, final Object obj) {
		providers.put(klass, new Provider<Object>() {

			@Override
			public Object get() {
				return obj;
			}

		});
	}

	public void removeProvider(Class<?> klass) {
		providers.remove(klass);
	}

	public void removeAllProviders() {
		providers.clear();
	}

	public void inject(Object obj) {
		for (Class<?> c = obj.getClass(); c != Object.class; c = c
				.getSuperclass()) {
			for (Field field : c.getDeclaredFields()) {
				if (field.getAnnotation(javax.inject.Inject.class) == null) {
					continue;
				}
				field.setAccessible(true);
				Provider<?> provider = providers.get(field.getType());
				if (provider == null)
					Logger.warn("Fails to inject " + field + " for " + obj);
				else
					try {
						field.set(obj, provider.get());
						Logger.debug(obj.getClass() + "." + field.getName() + " is injected.");
					} catch (Exception e) {
						Logger.error(e.getMessage());
					}
			}
		}
	}
}
