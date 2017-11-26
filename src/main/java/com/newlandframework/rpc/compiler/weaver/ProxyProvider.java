package com.newlandframework.rpc.compiler.weaver;

import com.newlandframework.rpc.compiler.intercept.Interceptor;
import com.newlandframework.rpc.compiler.invoke.InterceptorInvoker;
import com.newlandframework.rpc.compiler.invoke.ObjectInvoker;

public class ProxyProvider implements ClassProxy {
	private static final ClassCache PROXY_CLASS_CACHE = new ClassCache(new ByteCodeClassTransformer());

	@Override
	public <T> T createProxy(ClassLoader classLoader, Object target, Interceptor interceptor, Class<?>... proxyClasses) throws ReflectiveOperationException {
		return createProxy(classLoader, new InterceptorInvoker(target, interceptor), proxyClasses);
	}

	private <T> T createProxy(ClassLoader classLoader, ObjectInvoker invoker, final Class<?>... proxyClasses) throws ReflectiveOperationException {
		Class<?> proxyClass = PROXY_CLASS_CACHE.getProxyClass(classLoader, proxyClasses);
		T result = (T) proxyClass.getConstructor(ObjectInvoker.class).newInstance(invoker);
		return result;
	}
}
