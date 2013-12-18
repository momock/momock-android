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
		SQLiteDatabase dbMem = null;
		JsonDatabase jdb;

		Collection(JsonDatabase db, MySQLiteOpenHelper helper, String name) {
			this.jdb = db;
			this.helper = helper;
			this.name = name;
		}

		public JSONObject get(String id) {
			if (id == null) return null;
			JSONObject jo = null;
			String sql = "select * from data where id=? and name=?;";
			SQLiteDatabase db = jdb.getNativeDatabase();
			if (db == null) return null;
			try{
				Cursor cursor = db.rawQuery(sql, new String[] { id, name });
				if (cursor != null) {
					if (cursor.getCount() == 1) {
						cursor.moveToNext();
						String json = cursor.getString(IDX_JSON);
						jo = parse(json);
					}
					cursor.close();
				}
			}catch(Exception e){
				Logger.error(e);
			}
			return jo;
		}

		public String set(String id, JSONObject jo) {
			SQLiteDatabase db = jdb.getNativeDatabase();
			if (db == null) return null;
			try{
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
			}catch(Exception e){
				Logger.error(e);
			}
			return id;
		}
		public List<Document> list() {
			return list(null, false, 0);
		}
		public List<Document> list(IFilter filter, boolean delayLoad, int max) {
			List<Document> rows = new ArrayList<Document>();
			String sql = "select * from data where name=?";
			SQLiteDatabase db = jdb.getNativeDatabase();
			if (db == null) return rows;
			try{
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
			}catch(Exception e){
				Logger.error(e);
			}
			return rows;
		}
	}

	private static class MySQLiteOpenHelper extends SQLiteOpenHelper {

		public MySQLiteOpenHelper(Context context, String name) {
			super(context, name, null, 1);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			String sql = "create table data(id text,name text,json text, primary key(name, id));";
			Logger.debug("Create Json Database");
			db.execSQL(sql);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		}

	}
		
	abstract SQLiteDatabase getNativeDatabase();
	public abstract Collection getCollection(String name);
	public abstract void forceClose();

	public static JsonDatabase get(Context context) {
		return get(context, null);
	}

	public static JsonDatabase get(final Context context, final String dbname) {
		return new JsonDatabase() {
			Map<String, Collection> cols = new HashMap<String, Collection>();
			SQLiteDatabase db = null;
			MySQLiteOpenHelper helper = new MySQLiteOpenHelper(context, dbname);
			
			@Override
			public Collection getCollection(String name) {
				if (!cols.containsKey(name)) {
					cols.put(name, new Collection(this, helper, name));
				}
				return cols.get(name);
			}

			@Override
			public void forceClose() {
				if (db != null){
					db.close();
					db = null;
				}
			}

			@Override
			SQLiteDatabase getNativeDatabase() {
				if (db == null || !db.isOpen()) {
					try{
						db = helper.getWritableDatabase();
					}catch(Exception e){
						Logger.error(e);
						Logger.debug("Database Path :" + context.getDatabasePath(dbname).getPath());
						try{
							helper = new MySQLiteOpenHelper(context, null);
							db = helper.getWritableDatabase();
						}catch(Exception ex){
							Logger.error(ex);
							db = null;
						}
					}
				}
				return db;
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
