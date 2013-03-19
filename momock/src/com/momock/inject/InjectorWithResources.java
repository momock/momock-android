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

import javax.inject.Named;
import javax.inject.Provider;

import android.content.Context;
import android.content.res.Resources;

import com.momock.util.Logger;

public class InjectorWithResources extends Injector{
	@Override
	public void inject(Object obj) {
		Resources resources = getObject(Resources.class);
		Context context = getObject(Context.class);
		for (Class<?> c = obj.getClass(); c != Object.class; c = c
				.getSuperclass()) {
			for (Field field : c.getDeclaredFields()) {
				Named named = field.getAnnotation(javax.inject.Named.class);
				if (named != null && field.getType() == int.class && resources != null && context != null){
					String[] parts = named.value().trim().split("\\.");
					String type = "id";
					String name = null;
					if (parts.length == 1){
						name = parts[0];
					} else if (parts.length == 2){
						type = parts[0];
						name = parts[1];
					} else if (parts.length == 3){
						type = parts[1];
						name = parts[2];
					} else{
						Logger.error(named.value() + " is not a valid resource name!");
						continue;
					}						
					int id = resources.getIdentifier(name, type, context.getPackageName());
					field.setAccessible(true);
					try {
						field.set(obj, id);
					} catch (Exception e) {
						Logger.error(e);
					}
					continue;
				}
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
						//Logger.debug(obj.getClass() + "." + field.getName() + " is injected.");
					} catch (Exception e) {
						Logger.error(e);
					}
			}
		}
	}
}
