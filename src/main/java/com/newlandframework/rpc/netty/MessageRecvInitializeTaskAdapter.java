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
		//default adapter
	}

	@Override
	protected void injectSuccInvoke(long invokeTimespan) {
		//default adapter
	}

	@Override
	protected void injectFailInvoke(Throwable error) {
		//default adapter
	}

	@Override
	protected void injectFilterInvoke() {
		//default adapter
	}

	@Override
	protected void acquire() {
		//default adapter
	}

	@Override
	protected void release() {
		//default adapter
	}
}
