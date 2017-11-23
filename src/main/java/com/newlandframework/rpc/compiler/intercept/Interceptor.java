package com.newlandframework.rpc.compiler.intercept;

import java.lang.reflect.InvocationTargetException;

public interface Interceptor {
	Object intercept(Invocation invocation) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException;
}
