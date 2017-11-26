package com.newlandframework.rpc.netty;

import java.util.Map;

import com.google.common.collect.ClassToInstanceMap;
import com.google.common.collect.MutableClassToInstanceMap;
import com.newlandframework.rpc.netty.handler.HessianRecvHandler;
import com.newlandframework.rpc.netty.handler.JdkNativeRecvHandler;
import com.newlandframework.rpc.netty.handler.KryoRecvHandler;
import com.newlandframework.rpc.netty.handler.NettyRpcRecvHandler;
import com.newlandframework.rpc.netty.handler.ProtostuffRecvHandler;
import com.newlandframework.rpc.serialize.RpcSerializeFrame;
import com.newlandframework.rpc.serialize.RpcSerializeProtocol;

import io.netty.channel.ChannelPipeline;

public class RpcRecvSerializeFrame implements RpcSerializeFrame {

	private Map<String, Object> handlerMap = null;

	public RpcRecvSerializeFrame(Map<String, Object> handlerMap) {
		this.handlerMap = handlerMap;
	}

	private static ClassToInstanceMap<NettyRpcRecvHandler> handler = MutableClassToInstanceMap.create();

	static {
		handler.putInstance(JdkNativeRecvHandler.class, new JdkNativeRecvHandler());
		handler.putInstance(KryoRecvHandler.class, new KryoRecvHandler());
		handler.putInstance(HessianRecvHandler.class, new HessianRecvHandler());
		handler.putInstance(ProtostuffRecvHandler.class, new ProtostuffRecvHandler());
	}

	@Override
	public void select(RpcSerializeProtocol protocol, ChannelPipeline pipeline) {
		switch (protocol) {
		case JDKSERIALIZE:
			addHandlersToPipeline(JdkNativeRecvHandler.class, pipeline);
			break;
		case KRYOSERIALIZE:
			addHandlersToPipeline(KryoRecvHandler.class, pipeline);
			break;
		case HESSIANSERIALIZE:
			addHandlersToPipeline(HessianRecvHandler.class, pipeline);
			break;
		case PROTOSTUFFSERIALIZE:
			addHandlersToPipeline(ProtostuffRecvHandler.class, pipeline);
			break;
		}
	}

	private void addHandlersToPipeline(Class<? extends NettyRpcRecvHandler> handlerType, ChannelPipeline pipeline) {
		handler.getInstance(handlerType).handle(handlerMap, pipeline);
	}
}
