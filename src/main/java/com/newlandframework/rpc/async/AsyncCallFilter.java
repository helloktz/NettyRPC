package com.newlandframework.rpc.async;

import java.lang.reflect.Method;

import net.sf.cglib.proxy.CallbackFilter;

public class AsyncCallFilter implements CallbackFilter {
	@Override
	public int accept(Method method) {
		return AsyncCallObject.class.isAssignableFrom(method.getDeclaringClass()) ? 1 : 0;
	}
}
