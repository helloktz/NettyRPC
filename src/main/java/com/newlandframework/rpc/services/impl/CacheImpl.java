package com.newlandframework.rpc.services.impl;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.newlandframework.rpc.services.Cache;

public class CacheImpl implements Cache {
	private final Map<Object, Object> store;

	public CacheImpl() {
		final int max = 256;
		this.store = new LinkedHashMap<Object, Object>() {
			private static final long serialVersionUID = 1L;

			@Override
			protected boolean removeEldestEntry(Entry<Object, Object> eldest) {
				return size() > max;
			}
		};
	}

	@Override
	public void put(Object key, Object value) {
		store.put(key, value);
	}

	@Override
	public Object get(Object key) {
		return store.get(key);
	}
}
