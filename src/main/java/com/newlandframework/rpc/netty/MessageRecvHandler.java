package com.newlandframework.rpc.netty;

import java.util.Map;
import java.util.concurrent.Callable;

import com.newlandframework.rpc.model.MessageRequest;
import com.newlandframework.rpc.model.MessageResponse;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class MessageRecvHandler extends ChannelInboundHandlerAdapter {

	private final Map<String, Object> handlerMap;

	public MessageRecvHandler(Map<String, Object> handlerMap) {
		this.handlerMap = handlerMap;
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		MessageRequest request = (MessageRequest) msg;
		MessageResponse response = new MessageResponse();
		RecvInitializeTaskFacade facade = new RecvInitializeTaskFacade(request, response, handlerMap);
		Callable<Boolean> recvTask = facade.getTask();
		MessageRecvExecutor.submit(recvTask, ctx, request, response);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		log.error(cause);
		ctx.close();
	}
}
