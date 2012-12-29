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
package com.momock.cache;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import com.momock.util.Logger;

import android.graphics.Bitmap;
import android.os.Debug;

public class BitmapCache<K> implements ICache<K, Bitmap> {
	static class LinkedHashMapCache<K> extends LinkedHashMap<K, Bitmap> {

		private static final long serialVersionUID = 1L;

	    private static final int INITIAL_CAPACITY = 32;

	    private static final float LOAD_FACTOR = 0.75f;

	    private final long maxBytes;

	    private boolean removeEldest;
	    
	    public LinkedHashMapCache(long maxBytes) {
	        super(INITIAL_CAPACITY, LOAD_FACTOR, true);
	        this.maxBytes = maxBytes;
	    }

	    static long sizeOf(Bitmap b) {
	        return b.getRowBytes() * b.getHeight();
	    }

	    private static long sizeOf(Iterable<Bitmap> bitmaps) {
	        long total = 0;
	        for (Bitmap bitmap : bitmaps) {
	            total += sizeOf(bitmap);
	        }
	        return total;
	    }

	    @Override
	    protected boolean removeEldestEntry(java.util.Map.Entry<K, Bitmap> eldest) {
	    	if (removeEldest) {
	    		Logger.debug("Removing " + eldest.getKey() + " from cache!");
	    		eldest.getValue().recycle();
	    	}
	        return removeEldest;
	    }

	    private void trimEldest() {
	        super.remove(null);

	        removeEldest = true;
	        try {
	            super.put(null, null);
	        } finally {
	        	removeEldest = false;
	        }

	        super.remove(null);
	    }

	    private void trim() {
	    	Logger.debug("Current bitmap cache size : " + (sizeOf(values()) / 1024) + "K / " + (Debug.getNativeHeapAllocatedSize() / 1024) + "K");
	        while (sizeOf(values()) > maxBytes) {
	            trimEldest();
	        }
	    }

	    private NullPointerException nullKeyException() {
	        return new NullPointerException("Key is null");
	    }

	    @Override
	    public Bitmap put(K key, Bitmap value) {
	        if (key == null) {
	            throw nullKeyException();
	        }
	        try {
	            return super.put(key, value);
	        } finally {
	            trim();
	        }
	    }

	    @Override
	    public void putAll(Map<? extends K, ? extends Bitmap> map) {
	        if (map.containsKey(null)) {
	            throw nullKeyException();
	        }
	        try {
	            super.putAll(map);
	        } finally {
	            trim();
	        }
	    }

	    @Override
	    public Bitmap get(Object key) {
	        if (key == null) {
	            throw nullKeyException();
	        }
	        return super.get(key);
	    }

	    @Override
	    public boolean containsKey(Object key) {
	        if (key == null) {
	            throw nullKeyException();
	        }
	        return super.containsKey(key);
	    }

	    @Override
	    public Bitmap remove(Object key) {
	        if (key == null) {
	            throw nullKeyException();
	        }
	        return super.remove(key);
	    }
	}
	LinkedHashMapCache<K> cache;
	public BitmapCache(long maxBytes){
		cache = new LinkedHashMapCache<K>(maxBytes);
	}
	@Override
	public boolean put(K key, Bitmap value) {
		cache.put(key, value);
		return true;
	}

	@Override
	public Bitmap get(K key) {
		return cache.get(key);
	}

	@Override
	public void remove(K key) {
		cache.remove(key);
	}

	@Override
	public Collection<K> keys() {
		return cache.keySet();
	}

	@Override
	public void clear() {
		cache.clear();
	}

}
