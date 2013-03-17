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

public interface IRService extends IService{
	Integer get(String type, String name);
	Integer get(String type, String name, Integer defaultVal);
	Integer getAnim(String name);
	Integer getAnim(String name, Integer defaultVal);
	Integer getAttr(String name);
	Integer getAttr(String name, Integer defaultVal);
	Integer getColor(String name);
	Integer getColor(String name, Integer defaultVal);
	Integer getDimen(String name);
	Integer getDimen(String name, Integer defaultVal);
	Integer getDrawable(String name);
	Integer getDrawable(String name, Integer defaultVal);
	Integer getId(String name);
	Integer getId(String name, Integer defaultVal);
	Integer getLayout(String name);
	Integer getLayout(String name, Integer defaultVal);
	Integer getString(String name);
	Integer getString(String name, Integer defaultVal);
	Integer getStyle(String name);
	Integer getStyle(String name, Integer defaultVal);
}
