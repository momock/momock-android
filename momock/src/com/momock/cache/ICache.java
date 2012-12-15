package com.momock.cache;

import java.util.Collection;

public interface ICache<K, V> {

	boolean put(K key, V value);

	V get(K key);

	void remove(K key);

	Collection<K> keys();

	void clear();
}
