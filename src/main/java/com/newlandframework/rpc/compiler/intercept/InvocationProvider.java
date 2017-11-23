package com.newlandframework.rpc.compiler.intercept;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.commons.lang3.ArrayUtils;

import lombok.Getter;

public class InvocationProvider implements Invocation {
	@Getter
	private final Method method;
	@Getter
	private final Object[] arguments;
	@Getter
	private final Object proxy;
	private final Object target;

	public InvocationProvider(final Object target, final Object proxy, final Method method, final Object[] arguments) {
		Object[] objects = ArrayUtils.clone(arguments);
		this.method = method;
		this.arguments = objects == null ? new Object[0] : objects;
		this.proxy = proxy;
		this.target = target;
	}

	@Override
	public Object proceed() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		return method.invoke(target, arguments);
	}
}
