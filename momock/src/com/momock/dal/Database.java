/*
 * Copyright (C) 2012-2013 momock.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.momock.dal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.momock.util.BeanHelper;
import com.momock.util.Logger;

public class Database extends SQLiteOpenHelper {

    private static final String INIT_CREATE_SQL = "database_init";
    
    private static final String VERSION_FOR_UPGRADE = "database_version";
        
    private SQLiteDatabase dbObj;
    
    private Context context;
    
    private boolean isUpgrade = false;
    
    public Database(Context context) {
        super(context, null, null, 1);
        this.context = context;
    }
    
    public Database(Context context, String dbName) {
        super(context, dbName, null, Integer.parseInt(context.getResources().getString(context.getResources().getIdentifier(VERSION_FOR_UPGRADE, "string", context.getPackageName()))));
        this.context = context;
    }
    
    @Override
    public void onCreate(SQLiteDatabase db) {
        int pointer = context.getResources().getIdentifier(INIT_CREATE_SQL, "string", context.getPackageName());
        if (pointer == 0) {
            Logger.error("undefined sql id - initialize");
        } else {
            String[] createTabelSqls = context.getResources().getString(pointer).split(";");
            for (String sql : createTabelSqls) {
                db.execSQL(sql + ";");
            }
        }
    }
    
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        isUpgrade = true;
    }
    
    public Map<String, Object> executeForMap(String sqlId, Map<String, Object> bindParams) {
    	List<Map<String, Object>> mapList = executeForMapList(sqlId, bindParams);
    	if (mapList == null || mapList.size() == 0) return null;
        return mapList.get(0);
    }
    
    public List<Map<String, Object>> executeForMapList(String sqlId, Map<String, Object> bindParams) {
        getDbObject();
        int pointer = context.getResources().getIdentifier(sqlId, "string", context.getPackageName());
        if (pointer == 0) {
            Logger.error("undefined sql id");
            return null;
        }
        String sql = context.getResources().getString(pointer);
        if (bindParams != null) {
            Iterator<String> mapIterator = bindParams.keySet().iterator();
            while (mapIterator.hasNext()) {
                String key = mapIterator.next();
                Object value = bindParams.get(key);
                sql = sql.replaceAll("#" + key.toLowerCase() + "#", value == null ? null : "'" + value.toString() + "'");
            }
        }
        if (sql.indexOf('#') != -1) {
            Logger.error("undefined parameter");
            return null;
        }
        Cursor cursor = dbObj.rawQuery(sql, null);
        List<Map<String, Object>> mapList = new ArrayList<Map<String,Object>>();
        if (cursor == null) {
            return null;
        }
        String[] columnNames = cursor.getColumnNames();
        while(cursor.moveToNext()) {
            Map<String, Object> map = new HashMap<String, Object>();
            int i = 0;
            for (String columnName : columnNames) {
                map.put(columnName, cursor.getString(i));
                i++;
            }
            mapList.add(map);
        }
        cursor.close();
        dbObj.close();
        return mapList;
    }
    public <T> T executeForBean(String sqlId, Map<String, Object> bindParams, Class<T> bean) {
    	List<T> objectList = executeForBeanList(sqlId, bindParams, bean);
    	if (objectList == null || objectList.size() == 0) return null;
        return objectList.get(0);
    }
    public <T>List<T> executeForBeanList(String sqlId, Map<String, Object> bindParams, Class<T> bean) {
        getDbObject();
        int pointer = context.getResources().getIdentifier(sqlId, "string", context.getPackageName());
        if (pointer == 0) {
            Logger.error("undefined sql id");
            return null;
        }
        String sql = context.getResources().getString(pointer);
        if (bindParams != null) {
            Iterator<String> mapIterator = bindParams.keySet().iterator();
            while (mapIterator.hasNext()) {
                String key = mapIterator.next();
                Object value = bindParams.get(key);
                sql = sql.replaceAll("#" + key.toLowerCase() + "#", value == null ? null : "'" + value.toString() + "'");
            }
        }
        if (sql.indexOf('#') != -1) {
            Logger.error("undefined parameter");
            return null;
        }
        Cursor cursor = dbObj.rawQuery(sql, null);
        List<T> objectList = new ArrayList<T>();
        if (cursor == null) {
            return null;
        }
        String[] columnNames = cursor.getColumnNames();
        List<String> dataNames = new ArrayList<String>();
        for (String columnName : columnNames) {
            dataNames.add(columnName);
        } 
        T beanObj = null;
        while(cursor.moveToNext()) {
            Map<String, Object> map = new HashMap<String, Object>();
            int i = 0;
            for (String dataName : dataNames) {
                map.put(dataName, cursor.getString(i));
                i++;
            }
            try {
                beanObj = (T)bean.newInstance();
                BeanHelper.copyPropertiesFromMap(beanObj, map);
            } catch (Exception e) {
                Logger.debug(e.toString());
                return null;
            } 
            objectList.add(beanObj);
        }
        cursor.close();
        dbObj.close();
        return objectList;
    }
    
    public int execute(String sqlId, Map<String, Object> bindParams) {
        getDbObject();
        int row = 0;
        int pointer = context.getResources().getIdentifier(sqlId, "string", context.getPackageName());
        if (pointer == 0) {
            Logger.error("undefined sql id");
            return row;
        }
        String sql = context.getResources().getString(pointer);
        if (bindParams != null) {
            Iterator<String> mapIterator = bindParams.keySet().iterator();
            while (mapIterator.hasNext()) {
                String key = mapIterator.next();
                Object value = bindParams.get(key);
                sql = sql.replaceAll("#" + key.toLowerCase() + "#", value == null ? null : "'" + value.toString() + "'");
            }
        }
        if (sql.indexOf('#') != -1) {
            Logger.error("undefined parameter");
            return row;
        }
        try {
            dbObj.execSQL(sql);
            dbObj.close();
            row += 1;
        } catch (SQLException e) {
            return row;
        }
        return row;
    }
    
    private SQLiteDatabase getDbObject() {
        if (dbObj == null || !dbObj.isOpen()) {
            dbObj = getWritableDatabase();
        }
        return dbObj;
    }
    
    public boolean isUpgrade() {
        Logger.debug(String.valueOf(getDbObject().getVersion())); 
        return isUpgrade;
    }
}
