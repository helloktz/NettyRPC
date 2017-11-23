package com.newlandframework.rpc.netty.handler;

import java.util.Map;

import io.netty.channel.ChannelPipeline;

public interface NettyRpcRecvHandler {
	void handle(Map<String, Object> handlerMap, ChannelPipeline pipeline);
}
