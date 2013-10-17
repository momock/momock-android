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
package com.momock.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public abstract class JsonDatabase {
	static final int IDX_ID = 0;
	static final int IDX_NAME = 1;
	static final int IDX_JSON = 2;

	public static class Document {
		Collection col;
		String id;
		JSONObject jo;

		Document(Collection col, String id, JSONObject jo) {
			this.col = col;
			this.id = id;
			this.jo = jo;
		}

		public String getId() {
			return id;
		}

		public JSONObject getData() {
			if (jo == null) {
				jo = col.get(id);
			}
			return jo;
		}
	}

	public static interface IFilter {
		boolean check(String id, JSONObject doc);
	}

	public static class Collection {
		MySQLiteOpenHelper helper;
		String name;
		SQLiteDatabase db = null;

		Collection(MySQLiteOpenHelper helper, String name) {
			this.helper = helper;
			this.name = name;
		}

		void begin() {
			if (db == null || !db.isOpen()) {
				db = helper.getWritableDatabase();
			}
		}

		void end() {
			if (helper.getName() != null)
				db.close();
		}

		public JSONObject get(String id) {
			JSONObject jo = null;
			String sql = "select * from data where id=? and name=?;";
			begin();
			Cursor cursor = db.rawQuery(sql, new String[] { id, name });
			if (cursor != null) {
				if (cursor.getCount() == 1) {
					cursor.moveToNext();
					String json = cursor.getString(IDX_JSON);
					jo = parse(json);
				}
				cursor.close();
			}
			end();
			return jo;
		}

		public String set(String id, JSONObject jo) {
			begin();
			if (id == null)
				id = UUID.randomUUID().toString();
			if (jo == null) {
				db.delete("data", "id=? and name=?", new String[] { id, name });
			} else {
				String sql = "select * from data where id=? and name=?;";
				Cursor cursor = db.rawQuery(sql, new String[] { id, name });
				String json = jo.toString();
				boolean exists = cursor != null && cursor.getCount() == 1;
				Logger.debug("exists " + exists + ":" + cursor.getCount());
				cursor.close();
				if (exists) {
					ContentValues values = new ContentValues();
					values.put("json", json);
					db.update("data", values, "id=? and name=?", new String[] {
							id, name });
				} else {
					ContentValues values = new ContentValues();
					values.put("id", id);
					values.put("name", name);
					values.put("json", json);
					db.insert("data", "", values);
				}
			}
			end();
			return id;
		}

		public List<Document> list(IFilter filter, boolean delayLoad, int max) {
			List<Document> rows = new ArrayList<Document>();
			String sql = "select * from data where name=?";
			begin();
			Cursor cursor = db.rawQuery(sql, new String[] { name });
			if (cursor != null) {
				while (cursor.moveToNext()) {
					String id = cursor.getString(IDX_ID);
					if (filter == null) {
						rows.add(new Document(this, id, delayLoad ? null
								: parse(cursor.getString(IDX_JSON))));
					} else {
						String json = cursor.getString(IDX_JSON);
						JSONObject jo = parse(json);
						if (filter.check(id, jo)) {
							rows.add(new Document(this, id, delayLoad ? null : jo));
						}
					}
					if (max > 0 && rows.size() >= max) break;
				}
				cursor.close();
			}
			end();
			return rows;
		}
	}

	private static class MySQLiteOpenHelper extends SQLiteOpenHelper {
		String name;

		public MySQLiteOpenHelper(Context context, String name) {
			super(context, name, null, 1);
			this.name = name;
		}

		public String getName() {
			return name;
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			String sql = "create table data(id text,name text,json text, primary key(name, id));";
			Logger.debug("Create Json Database : " + sql);
			db.execSQL(sql);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		}

	}

	public abstract Collection getCollection(String name);

	public static JsonDatabase get(Context context) {
		return get(context, null);
	}

	public static JsonDatabase get(Context context, String name) {
		final MySQLiteOpenHelper helper = new MySQLiteOpenHelper(context, name);
		return new JsonDatabase() {
			Map<String, Collection> cols = new HashMap<String, Collection>();

			@Override
			public Collection getCollection(String name) {
				if (!cols.containsKey(name)) {
					cols.put(name, new Collection(helper, name));
				}
				return cols.get(name);
			}

		};
	}

	static JSONObject parse(String json) {
		JSONTokener tokener = new JSONTokener(json);
		Object root;
		try {
			root = tokener.nextValue();
		} catch (JSONException e) {
			Logger.error(e);
			return null;
		}
		return (JSONObject) root;
	}
}
