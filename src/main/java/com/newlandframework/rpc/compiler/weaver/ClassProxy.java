package com.newlandframework.rpc.compiler.weaver;

import com.newlandframework.rpc.compiler.intercept.Interceptor;

public interface ClassProxy {
	<T> T createProxy(Object target, Interceptor interceptor, Class<?>... proxyClasses);

	<T> T createProxy(ClassLoader classLoader, Object target, Interceptor interceptor, Class<?>... proxyClasses);
}
