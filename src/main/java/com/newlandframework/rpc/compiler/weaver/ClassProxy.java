package com.newlandframework.rpc.compiler.weaver;

import com.newlandframework.rpc.compiler.intercept.Interceptor;

public interface ClassProxy {
	default <T> T createProxy(Object target, Interceptor interceptor, Class<?>... proxyClasses) throws ReflectiveOperationException {
		return createProxy(Thread.currentThread().getContextClassLoader(), target, interceptor, proxyClasses);
	}

	<T> T createProxy(ClassLoader classLoader, Object target, Interceptor interceptor, Class<?>... proxyClasses) throws ReflectiveOperationException;
}
