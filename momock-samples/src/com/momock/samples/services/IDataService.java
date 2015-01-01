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
package com.momock.samples.services;

import com.momock.data.IDataMutableList;
import com.momock.samples.model.Category;
import com.momock.samples.model.Product;
import com.momock.service.IService;

public interface IDataService extends IService{
	Product getProductById(int id);

	Category getCategoryById(int id);

	IDataMutableList<Category> getAllCategories();

	IDataMutableList<Product> getProductsInCategory(int cid);

	IDataMutableList<Product> getAllProducts();
}
