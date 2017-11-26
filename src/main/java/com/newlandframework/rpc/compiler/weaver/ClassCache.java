package com.newlandframework.rpc.compiler.weaver;

import java.util.Set;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Sets;

import lombok.SneakyThrows;

/**
 * 弱引用减少内存消耗
 * 
 * @author helloktz
 *
 */
public class ClassCache {
	private final LoadingCache<ClassLoader, Cache<Set<Class<?>>, Class<?>>> loader = CacheBuilder.newBuilder().build(new CacheLoader<ClassLoader, Cache<Set<Class<?>>, Class<?>>>() {
		@Override
		public Cache<Set<Class<?>>, Class<?>> load(ClassLoader key) throws Exception {
			return CacheBuilder.newBuilder().weakValues().initialCapacity(512).build();
		}

	});

	private final Transformer transformer;

	public ClassCache(Transformer transformer) {
		this.transformer = transformer;
	}

	private Cache<Set<Class<?>>, Class<?>> getClassCache(ClassLoader classLoader) {
		return loader.getUnchecked(classLoader);
	}

	@SneakyThrows
	public Class<?> getProxyClass(ClassLoader classLoader, Class<?>[] proxyClasses) {
		Cache<Set<Class<?>>, Class<?>> classCache = getClassCache(classLoader);
		Set<Class<?>> key = Sets.newHashSet(proxyClasses);
		Class<?> proxyClass = classCache.get(key, () -> transformer.transform(classLoader, proxyClasses));

		return proxyClass;
	}
}
