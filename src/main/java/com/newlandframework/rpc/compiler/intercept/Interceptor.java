package com.newlandframework.rpc.compiler.intercept;

public interface Interceptor {
	Object intercept(Invocation invocation) throws ReflectiveOperationException;
}
