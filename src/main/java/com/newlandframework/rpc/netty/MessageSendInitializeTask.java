package com.newlandframework.rpc.netty;

import java.net.InetSocketAddress;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import com.newlandframework.rpc.core.RpcSystemConfig;
import com.newlandframework.rpc.serialize.RpcSerializeProtocol;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoop;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

public class MessageSendInitializeTask implements Callable<Boolean> {

	private EventLoopGroup eventLoopGroup = null;
	private InetSocketAddress serverAddress = null;
	private RpcSerializeProtocol protocol;

	MessageSendInitializeTask(EventLoopGroup eventLoopGroup, InetSocketAddress serverAddress, RpcSerializeProtocol protocol) {
		this.eventLoopGroup = eventLoopGroup;
		this.serverAddress = serverAddress;
		this.protocol = protocol;
	}

	@Override
	public Boolean call() {
		Bootstrap b = new Bootstrap();
		b.group(eventLoopGroup).channel(NioSocketChannel.class).option(ChannelOption.SO_KEEPALIVE, true).remoteAddress(serverAddress);
		b.handler(new MessageSendChannelInitializer().buildRpcSerializeProtocol(protocol));

		ChannelFuture channelFuture = b.connect();
		channelFuture.addListener(new ChannelFutureListener() {
			@Override
			public void operationComplete(final ChannelFuture channelFuture) throws Exception {
				if (channelFuture.isSuccess()) {
					MessageSendHandler handler = channelFuture.channel().pipeline().get(MessageSendHandler.class);
					RpcServerLoader.getInstance().setMessageSendHandler(handler);
				} else {
					EventLoop loop = (EventLoop) eventLoopGroup.schedule(new Runnable() {
						@Override
						public void run() {
							System.out.println("NettyRPC server is down,start to reconnecting to: " + serverAddress.getAddress().getHostAddress() + ':' + serverAddress.getPort());
							call();
						}
					}, RpcSystemConfig.SYSTEM_PROPERTY_CLIENT_RECONNECT_DELAY, TimeUnit.SECONDS);
				}
			}
		});
		return Boolean.TRUE;
	}
}
