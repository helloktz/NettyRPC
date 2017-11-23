package com.newlandframework.rpc.netty.handler;

import java.util.Map;

import com.newlandframework.rpc.netty.MessageRecvHandler;
import com.newlandframework.rpc.serialize.hessian.HessianCodecUtil;
import com.newlandframework.rpc.serialize.hessian.HessianDecoder;
import com.newlandframework.rpc.serialize.hessian.HessianEncoder;

import io.netty.channel.ChannelPipeline;

public class HessianRecvHandler implements NettyRpcRecvHandler {
	@Override
	public void handle(Map<String, Object> handlerMap, ChannelPipeline pipeline) {
		HessianCodecUtil util = new HessianCodecUtil();
		pipeline.addLast(new HessianEncoder(util));
		pipeline.addLast(new HessianDecoder(util));
		pipeline.addLast(new MessageRecvHandler(handlerMap));
	}
}
