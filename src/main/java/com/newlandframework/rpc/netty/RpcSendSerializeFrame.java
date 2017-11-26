package com.newlandframework.rpc.netty;

import com.google.common.collect.ClassToInstanceMap;
import com.google.common.collect.MutableClassToInstanceMap;
import com.newlandframework.rpc.netty.handler.HessianSendHandler;
import com.newlandframework.rpc.netty.handler.JdkNativeSendHandler;
import com.newlandframework.rpc.netty.handler.KryoSendHandler;
import com.newlandframework.rpc.netty.handler.NettyRpcSendHandler;
import com.newlandframework.rpc.netty.handler.ProtostuffSendHandler;
import com.newlandframework.rpc.serialize.RpcSerializeFrame;
import com.newlandframework.rpc.serialize.RpcSerializeProtocol;

import io.netty.channel.ChannelPipeline;

public class RpcSendSerializeFrame implements RpcSerializeFrame {
	private static ClassToInstanceMap<NettyRpcSendHandler> handler = MutableClassToInstanceMap.create();

	static {
		handler.putInstance(JdkNativeSendHandler.class, new JdkNativeSendHandler());
		handler.putInstance(KryoSendHandler.class, new KryoSendHandler());
		handler.putInstance(HessianSendHandler.class, new HessianSendHandler());
		handler.putInstance(ProtostuffSendHandler.class, new ProtostuffSendHandler());
	}

	@Override
	public void select(RpcSerializeProtocol protocol, ChannelPipeline pipeline) {
		switch (protocol) {
		case JDKSERIALIZE:
			addHandlersToPipeline(JdkNativeSendHandler.class, pipeline);
			break;
		case KRYOSERIALIZE:
			addHandlersToPipeline(KryoSendHandler.class, pipeline);
			break;
		case HESSIANSERIALIZE:
			addHandlersToPipeline(HessianSendHandler.class, pipeline);
			break;
		case PROTOSTUFFSERIALIZE:
			addHandlersToPipeline(ProtostuffSendHandler.class, pipeline);
			break;
		}
	}

	private void addHandlersToPipeline(Class<? extends NettyRpcSendHandler> handlerType, ChannelPipeline pipeline) {
		handler.getInstance(handlerType).handle(pipeline);
	}
}
