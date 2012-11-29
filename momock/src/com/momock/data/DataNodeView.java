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

import java.util.Arrays;
import java.util.Comparator;

public class DataNodeView extends DataViewBase<IDataNode> {
	String path = null;
	IDataNode node;

	public DataNodeView(IDataNode node) {
		this.node = node;
		refresh();
	}

	public DataNodeView(IDataNode node, String path) {
		this.path = path;
		this.node = node;
		refresh();
	}

	public DataNodeView(IDataNode node, String path, IFilter<IDataNode> filter) {
		this.path = path;
		this.node = node;
		this.filter = filter;
		refresh();
	}

	public DataNodeView(IDataNode node, String path, IFilter<IDataNode> filter,
			IOrder<IDataNode> order) {
		this.path = path;
		this.node = node;
		this.filter = filter;
		this.order = order;
		refresh();
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	void add(IDataNode n) {
		if (filter == null || filter.check(n)) {
			store.addItem(n);
		}
	}

	void visit(IDataNode current, String[] segments, int level) {
		if (segments != null && segments.length <= level)
			return;
		String name = segments[level];
		for (int i = 0; i < current.getItemCount(); i++) {
			Object obj = current.getItem(i);
			if (obj instanceof IDataNode) {
				IDataNode ni = (IDataNode) obj;
				if (segments.length - 1 == level) {
					add(ni);
				} else if (name.equals(ni.getName())) {
					visit(ni, segments, level + 1);
				}
			}
		}
	}

	@Override
	public void refresh() {
		store.removeAllItems();
		visit(node, path == null ? null : path.split("/"), 0);
		if (store.getItemCount() > 0 && order != null) {
			int count = store.getItemCount();
			IDataNode[] nodes = new IDataNode[count];
			for (int i = 0; i < count; i++) {
				nodes[i] = store.getItem(i);
			}
			Arrays.sort(nodes, new Comparator<IDataNode>() {
				@Override
				public int compare(IDataNode lhs, IDataNode rhs) {
					return order.compare(lhs, rhs);
				}
			});
			if (limit > 0){
				store.removeAllItems();
				for (int i = 0; i < limit; i++) {	
					if (offset + i < count)
						store.addItem(nodes[offset + i]);
					else 
						break;
				}
			} else {
				for (int i = 0; i < count; i++) {
					store.setItem(i, nodes[i]);
				}
			}
		}
	}
}
