package com.newlandframework.rpc.async;

import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

import com.newlandframework.rpc.core.ReflectionUtils;
import com.newlandframework.rpc.core.RpcSystemConfig;
import com.newlandframework.rpc.exception.AsyncCallException;
import com.newlandframework.rpc.parallel.RpcThreadPool;

public class AsyncInvoker {
	private ThreadPoolExecutor executor = RpcThreadPool.getExecutor(RpcSystemConfig.SYSTEM_PROPERTY_THREADPOOL_THREAD_NUMS, RpcSystemConfig.SYSTEM_PROPERTY_THREADPOOL_QUEUE_NUMS);

	public <R> R submit(AsyncCallback<R> callback) {
		Type type = callback.getClass().getGenericInterfaces()[0];
		if (type instanceof ParameterizedType) {
			Class<?> returnClass = ReflectionUtils.getGenericClass((ParameterizedType) type, 0);
			return intercept(callback, returnClass);
		} else {
			throw new AsyncCallException("NettyRPC AsyncCallback must be parameterized type!");
		}
	}

	private <T> AsyncFuture<T> submit0(Callable<T> task) {
		AsyncFuture<T> future = new AsyncFuture<>(task);
		executor.submit(future);
		return future;
	}

	private <R> R intercept(AsyncCallback<R> callback, Class<?> returnClass) {
		if (!Modifier.isPublic(returnClass.getModifiers())) {
			return callback.call();
		} else if (Modifier.isFinal(returnClass.getModifiers())) {
			return callback.call();
		} else if (Void.TYPE.isAssignableFrom(returnClass)) {
			return callback.call();
		} else if (returnClass.isPrimitive() || returnClass.isArray()) {
			return callback.call();
		} else if (returnClass == Object.class) {
			return callback.call();
		} else {
			return submit(callback, returnClass);
		}
	}

	private <R> R submit(final AsyncCallback<R> callback, Class<?> returnClass) {
		Future<R> future = submit0(() -> callback.call());

		AsyncCallResult result = new AsyncCallResult(returnClass, future, RpcSystemConfig.SYSTEM_PROPERTY_ASYNC_MESSAGE_CALLBACK_TIMEOUT);
		R asyncProxy = (R) result.getResult();

		return asyncProxy;
	}
}
