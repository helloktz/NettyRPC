package com.newlandframework.rpc.compiler.invoke;

import java.lang.reflect.Method;

import com.newlandframework.rpc.compiler.intercept.Interceptor;
import com.newlandframework.rpc.compiler.intercept.InvocationProvider;

public class InterceptorInvoker extends AbstractInvoker {
	private final Object target;
	private final Interceptor methodInterceptor;

	public InterceptorInvoker(Object target, Interceptor methodInterceptor) {
		this.target = target;
		this.methodInterceptor = methodInterceptor;
	}

	@Override
	public Object invokeImpl(Object proxy, Method method, Object[] args) throws ReflectiveOperationException {
		InvocationProvider invocation = new InvocationProvider(target, proxy, method, args);
		return methodInterceptor.intercept(invocation);
	}
}
