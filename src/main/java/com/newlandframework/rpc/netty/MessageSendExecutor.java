package com.newlandframework.rpc.netty;

import com.google.common.reflect.Reflection;
import com.newlandframework.rpc.serialize.RpcSerializeProtocol;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MessageSendExecutor {
	private static class MessageSendExecutorHolder {
		private static final MessageSendExecutor INSTANCE = new MessageSendExecutor();
	}

	public static MessageSendExecutor getInstance() {
		return MessageSendExecutorHolder.INSTANCE;
	}

	private RpcServerLoader loader = RpcServerLoader.getInstance();

	public MessageSendExecutor(String serverAddress, RpcSerializeProtocol serializeProtocol) {
		loader.load(serverAddress, serializeProtocol);
	}

	public void setRpcServerLoader(String serverAddress, RpcSerializeProtocol serializeProtocol) {
		loader.load(serverAddress, serializeProtocol);
	}

	public void stop() {
		loader.unLoad();
	}

	public <T> T execute(Class<T> rpcInterface) throws Exception {
		return Reflection.newProxy(rpcInterface, new MessageSendProxy<T>());
	}
}
