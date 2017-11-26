package com.newlandframework.rpc.services.impl;

import com.google.common.cache.CacheBuilder;
import com.newlandframework.rpc.services.Cache;

public class CacheImpl implements Cache {
	private final com.google.common.cache.Cache<Object, Object> store = CacheBuilder.newBuilder().maximumSize(256).build();

	@Override
	public void put(Object key, Object value) {
		store.put(key, value);
	}

	@Override
	public Object get(Object key) {
		return store.getIfPresent(key);
	}
}
