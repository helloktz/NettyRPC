package com.newlandframework.rpc.netty.handler;

import static com.newlandframework.rpc.serialize.MessageDecoder.MESSAGE_LENGTH;

import java.util.Map;

import com.newlandframework.rpc.netty.MessageRecvHandler;

import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

public class JdkNativeRecvHandler implements NettyRpcRecvHandler {
	@Override
	public void handle(Map<String, Object> handlerMap, ChannelPipeline pipeline) {
		pipeline.addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, MESSAGE_LENGTH, 0, MESSAGE_LENGTH));
		pipeline.addLast(new LengthFieldPrepender(MESSAGE_LENGTH));
		pipeline.addLast(new ObjectEncoder());
		pipeline.addLast(new ObjectDecoder(Integer.MAX_VALUE, ClassResolvers.weakCachingConcurrentResolver(this.getClass().getClassLoader())));
		pipeline.addLast(new MessageRecvHandler(handlerMap));
	}
}
