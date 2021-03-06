package com.newlandframework.rpc.netty;

import java.lang.reflect.Method;
import java.util.UUID;

import com.google.common.reflect.AbstractInvocationHandler;
import com.newlandframework.rpc.core.MessageCallBack;
import com.newlandframework.rpc.model.MessageRequest;

public class MessageSendProxy extends AbstractInvocationHandler {

	@Override
	public Object handleInvocation(Object proxy, Method method, Object[] args) throws InterruptedException {
		MessageRequest request = new MessageRequest();
		request.setMessageId(UUID.randomUUID().toString());
		request.setClassName(method.getDeclaringClass().getName());
		request.setMethodName(method.getName());
		request.setTypeParameters(method.getParameterTypes());
		request.setParametersVal(args);

		MessageSendHandler handler = RpcServerLoader.getInstance().getMessageSendHandler();
		MessageCallBack callBack = handler.sendRequest(request);
		return callBack.start();
	}
}
