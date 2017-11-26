package com.newlandframework.rpc.async;

import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.google.common.collect.Maps;
import com.newlandframework.rpc.core.ReflectionUtils;
import com.newlandframework.rpc.core.RpcSystemConfig;
import com.newlandframework.rpc.exception.AsyncCallException;
import com.newlandframework.rpc.exception.InvokeTimeoutException;

import net.sf.cglib.proxy.Callback;
import net.sf.cglib.proxy.Enhancer;

public class AsyncCallResult {
	private static final Map<String, Class<?>> PROXY_CACHE = Maps.newConcurrentMap();

	private Class<?> returnClass;
	private Future<?> future;
	private Long timeout;

	public AsyncCallResult(Class<?> returnClass, Future<?> future, Long timeout) {
		this.returnClass = returnClass;
		this.future = future;
		this.timeout = timeout;
	}

	public Object loadFuture() {
		try {
			if (timeout <= 0L) {
				return future.get();
			} else {
				return future.get(timeout, TimeUnit.MILLISECONDS);
			}
		} catch (TimeoutException e) {
			future.cancel(true);
			throw new AsyncCallException(e);
		} catch (InterruptedException e) {
			throw new AsyncCallException(e);
		} catch (Exception e) {
			translateTimeoutException(e);
			throw new AsyncCallException(e);
		}
	}

	private void translateTimeoutException(Exception t) {
		int index = t.getMessage().indexOf(RpcSystemConfig.TIMEOUT_RESPONSE_MSG);
		if (index != -1) {
			throw new InvokeTimeoutException(t);
		}
	}

	public Object getResult() {
		Class<?> proxyClass = PROXY_CACHE.get(returnClass.getName());
		if (proxyClass == null) {
			Enhancer enhancer = new Enhancer();
			if (returnClass.isInterface()) {
				enhancer.setInterfaces(new Class[] { AsyncCallObject.class, returnClass });
			} else {
				enhancer.setInterfaces(new Class[] { AsyncCallObject.class });
				enhancer.setSuperclass(returnClass);
			}
			enhancer.setCallbackFilter(new AsyncCallFilter());
			enhancer.setCallbackTypes(new Class[] { AsyncCallResultInterceptor.class, AsyncCallObjectInterceptor.class });
			proxyClass = enhancer.createClass();
			PROXY_CACHE.putIfAbsent(returnClass.getName(), proxyClass);
		}

		Enhancer.registerCallbacks(proxyClass, new Callback[] { new AsyncCallResultInterceptor(this), new AsyncCallObjectInterceptor(future) });

		try {
			return ReflectionUtils.newInstance(proxyClass);
		} finally {
			Enhancer.registerStaticCallbacks(proxyClass, null);
		}
	}
}
