package com.newlandframework.rpc.netty;

import java.lang.reflect.InvocationTargetException;

import org.apache.commons.lang3.reflect.MethodUtils;
import org.apache.commons.lang3.time.StopWatch;

import com.newlandframework.rpc.model.MessageRequest;

import lombok.Getter;
import lombok.Setter;

public class MethodInvoker {
	@Getter
	@Setter
	private Object serviceBean;
	private StopWatch sw = new StopWatch();

	public Object invoke(MessageRequest request) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
		String methodName = request.getMethodName();
		Object[] parameters = request.getParametersVal();
		sw.reset();
		sw.start();
		Object result = MethodUtils.invokeMethod(serviceBean, methodName, parameters);
		sw.stop();
		return result;
	}

	public long getInvokeTimespan() {
		return sw.getTime();
	}
}
