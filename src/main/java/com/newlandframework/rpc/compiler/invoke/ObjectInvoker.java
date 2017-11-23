package com.newlandframework.rpc.compiler.invoke;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public interface ObjectInvoker {
	Object invoke(Object proxy, Method method, Object... arguments) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException;
}
