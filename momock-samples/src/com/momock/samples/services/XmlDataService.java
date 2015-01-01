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

import java.io.IOException;

import com.momock.app.App;
import com.momock.data.DataList;
import com.momock.data.DataNodeView;
import com.momock.data.IDataList;
import com.momock.data.IDataMutableList;
import com.momock.data.IDataNode;
import com.momock.samples.model.Category;
import com.momock.samples.model.Product;
import com.momock.util.DataHelper;
import com.momock.util.Logger;

public class XmlDataService implements IDataService{
	DataList<Product> products = new DataList<Product>();
	DataList<Category> categories = new DataList<Category>();
	public XmlDataService(){
		try {
			int i; 
			IDataNode node = DataHelper.parseXml(App.get().getAssets().open("PreloadData.xml"));
			IDataList<IDataNode> cs = new DataNodeView(node, "Categories/Category").getData();
			for(i = 0; i < cs.getItemCount(); i++){
				Category c = new Category();
				c.copyPropertiesFrom(cs.getItem(i));
				categories.addItem(c);
			}
			IDataList<IDataNode> ps = new DataNodeView(node, "Products/Product").getData();
			for(i = 0; i < ps.getItemCount(); i++){
				Product p = new Product();
				p.copyPropertiesFrom(ps.getItem(i));
				products.addItem(p);
			}
		} catch (IOException e) {
			Logger.error(e.getMessage());
		}
	}

	@Override
	public Product getProductById(int id) {
		for(int i = 0; i < products.getItemCount(); i++)
		{
			Product p = products.getItem(i);
			if (p.getId() == id)
				return p;
		}
		return null;
	}
	@Override
	public Category getCategoryById(int id) {
		for(int i = 0; i < categories.getItemCount(); i++)
		{
			Category c = categories.getItem(i);
			if (c.getId() == id)
				return c;
		}
		return null;
	}
	@Override
	public IDataMutableList<Category> getAllCategories() {
		return categories;
	}
	@Override
	public IDataMutableList<Product> getProductsInCategory(int cid) {
		DataList<Product> ps = new DataList<Product>();
		for(int i = 0; i < products.getItemCount(); i++)
		{
			Product p = products.getItem(i);
			if (p.getCategoryId() == cid)
				ps.addItem(p);
		}
		return ps;
	}
	@Override
	public IDataMutableList<Product> getAllProducts() {
		return products;
	}

	@Override
	public void start() {
		
	}

	@Override
	public void stop() {
		
	}
	
	@Override
	public Class<?>[] getDependencyServices() {
		return null;
	}
	@Override
	public boolean canStop() {
		return true;
	}
}
