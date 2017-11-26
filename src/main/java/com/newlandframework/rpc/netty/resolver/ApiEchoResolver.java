package com.newlandframework.rpc.netty.resolver;

import java.util.concurrent.Callable;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ApiEchoResolver implements Callable<Boolean> {
	private static final boolean SSL = System.getProperty("ssl") != null;
	private String host;
	private int port;

	public ApiEchoResolver(String host, int port) {
		this.host = host;
		this.port = port;
	}

	@Override
	public Boolean call() {
		EventLoopGroup bossGroup = new NioEventLoopGroup(1);
		EventLoopGroup workerGroup = new NioEventLoopGroup();

		try {
			SslContext sslCtx = null;
			if (SSL) {
				SelfSignedCertificate ssc = new SelfSignedCertificate();
				sslCtx = SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey()).build();
			}

			ServerBootstrap b = new ServerBootstrap();
			b.option(ChannelOption.SO_BACKLOG, 1024);
			b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class).handler(new LoggingHandler(LogLevel.INFO)).childHandler(new ApiEchoInitializer(sslCtx));

			Channel ch = b.bind(port).sync().channel();

			log.info("You can open your web browser see NettyRPC server api interface: {}://{}:{}/NettyRPC.html", SSL ? "https" : "http", host, port);

			ch.closeFuture().sync();
			return Boolean.TRUE;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return Boolean.FALSE;
		} finally {
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}
	}
}
