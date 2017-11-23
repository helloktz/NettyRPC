package com.newlandframework.rpc.filter;

import java.lang.reflect.Method;

public interface Filter {
	// TODO: 2017/7/27 by tangjie
	// If returns false, indicates that the RPC request method is intercept.
	boolean before(Method method, Object processor, Object[] requestObjects);

	// If filter's before function returns false, filter's after function will not
	// be called.
	void after(Method method, Object processor, Object[] requestObjects);
}
