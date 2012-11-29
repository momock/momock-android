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
package com.momock.data;

import com.momock.util.Logger;

public abstract class DataViewBase<T> implements IDataView<T>{
	protected IDataMutableList<T> store = new DataList<T>();
	protected int offset = 0;
	protected int limit = -1;
	@Override
	public boolean hasItem(T item) {
		return store.hasItem(item);
	}

	@Override
	public T getItem(int index) {
		return store.getItem(index);
	}

	@Override
	public int getItemCount() {
		return store.getItemCount();
	}

	protected IFilter<T> filter = null;
	@Override
	public IFilter<T> getFilter() {
		return filter;
	}

	@Override
	public void setFilter(IFilter<T> filter) {
		this.filter = filter;
	}

	protected IOrder<T> order = null;
	@Override
	public IOrder<T> getOrder() {
		return order;
	}

	@Override
	public void setOrder(IOrder<T> order) {
		this.order = order;
	}

	@Override
	public int getOffset() {
		return offset;
	}

	@Override
	public void setOffset(int offset) {
		Logger.check(offset >= 0, "Offset must be positive!");
		this.offset = offset;		
	}

	@Override
	public int getLimit() {
		return limit;
	}

	@Override
	public void setLimit(int limit) {
		this.limit = limit;
	}

}
