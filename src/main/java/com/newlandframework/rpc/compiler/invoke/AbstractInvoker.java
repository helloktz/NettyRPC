package com.newlandframework.rpc.compiler.invoke;

import java.lang.reflect.Method;

import com.newlandframework.rpc.core.ReflectionUtils;

public abstract class AbstractInvoker implements ObjectInvoker {
	@Override
	public Object invoke(Object proxy, Method method, Object... args) throws ReflectiveOperationException {
		// FIXME: 2017/8/31 by tangjie Object类的方法没有必要字节码增强，这里直接判断返回
		if (ReflectionUtils.isHashCodeMethod(method)) {
			return Integer.valueOf(System.identityHashCode(proxy));
		}

		if (ReflectionUtils.isEqualsMethod(method)) {
			return Boolean.valueOf(proxy == args[0]);
		}

		return invokeImpl(proxy, method, args);
	}

	public abstract Object invokeImpl(Object proxy, Method method, Object[] args) throws ReflectiveOperationException;
}
