package com.newlandframework.rpc.netty;

import java.util.Map;

import com.newlandframework.rpc.model.MessageRequest;
import com.newlandframework.rpc.model.MessageResponse;

public class MessageRecvInitializeTaskAdapter extends AbstractMessageRecvInitializeTask {
	public MessageRecvInitializeTaskAdapter(MessageRequest request, MessageResponse response, Map<String, Object> handlerMap) {
		super(request, response, handlerMap);
	}

	@Override
	protected void injectInvoke() {

	}

	@Override
	protected void injectSuccInvoke(long invokeTimespan) {

	}

	@Override
	protected void injectFailInvoke(Throwable error) {

	}

	@Override
	protected void injectFilterInvoke() {

	}

	@Override
	protected void acquire() {

	}

	@Override
	protected void release() {

	}
}
