package com.momock.cache;

import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class SimpleCache<K, V> implements ICache<K, V>{
	private final Map<K, Reference<V>> softMap = Collections.synchronizedMap(new HashMap<K, Reference<V>>());

	@Override
	public boolean put(K key, V value) {
		softMap.put(key, createReference(value));
		return true;
	}

	@Override
	public V get(K key) {
		V result = null;
		Reference<V> reference = softMap.get(key);
		if (reference != null) {
			result = reference.get();
		}
		return result;
	}

	@Override
	public void remove(K key) {
		softMap.remove(key);
	}

	@Override
	public Collection<K> keys() {
		return softMap.keySet();
	}

	@Override
	public void clear() {
		softMap.clear();
	}


	protected Reference<V> createReference(V value){
		return new SoftReference<V>(value);
	}
}
