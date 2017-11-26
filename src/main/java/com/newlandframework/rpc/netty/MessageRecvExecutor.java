package com.newlandframework.rpc.netty;

import static com.newlandframework.rpc.core.RpcSystemConfig.DELIMITER;
import static com.newlandframework.rpc.core.RpcSystemConfig.IPADDR_OPRT_ARRAY_LENGTH;
import static com.newlandframework.rpc.core.RpcSystemConfig.RPC_ABILITY_DETAIL_SPI_ATTR;
import static com.newlandframework.rpc.core.RpcSystemConfig.RPC_COMPILER_SPI_ATTR;
import static com.newlandframework.rpc.core.RpcSystemConfig.SYSTEM_PROPERTY_JMX_METRICS_SUPPORT;
import static com.newlandframework.rpc.core.RpcSystemConfig.SYSTEM_PROPERTY_PARALLEL;
import static com.newlandframework.rpc.core.RpcSystemConfig.SYSTEM_PROPERTY_THREADPOOL_QUEUE_NUMS;
import static com.newlandframework.rpc.core.RpcSystemConfig.SYSTEM_PROPERTY_THREADPOOL_THREAD_NUMS;
import static com.newlandframework.rpc.core.RpcSystemConfig.isMonitorServerSupport;
import static com.newlandframework.rpc.parallel.RpcThreadPool.getExecutor;
import static com.newlandframework.rpc.parallel.RpcThreadPool.getExecutorWithJmx;
import static io.netty.channel.ChannelOption.SO_BACKLOG;
import static io.netty.channel.ChannelOption.SO_KEEPALIVE;

import java.nio.channels.spi.SelectorProvider;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.newlandframework.rpc.compiler.AccessAdaptiveProvider;
import com.newlandframework.rpc.core.AbilityDetailProvider;
import com.newlandframework.rpc.jmx.ModuleMetricsHandler;
import com.newlandframework.rpc.model.MessageKeyVal;
import com.newlandframework.rpc.model.MessageRequest;
import com.newlandframework.rpc.model.MessageResponse;
import com.newlandframework.rpc.netty.resolver.ApiEchoResolver;
import com.newlandframework.rpc.parallel.NamedThreadFactory;
import com.newlandframework.rpc.serialize.RpcSerializeProtocol;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MessageRecvExecutor implements ApplicationContextAware {

	@Getter
	@Setter
	private String serverAddress;
	@Getter
	@Setter
	private int echoApiPort;
	@Getter
	@Setter
	private RpcSerializeProtocol serializeProtocol = RpcSerializeProtocol.JDKSERIALIZE;
	private static final int PARALLEL = SYSTEM_PROPERTY_PARALLEL * 2;
	private static int threadNums = SYSTEM_PROPERTY_THREADPOOL_THREAD_NUMS;
	private static int queueNums = SYSTEM_PROPERTY_THREADPOOL_QUEUE_NUMS;
	private static volatile ListeningExecutorService executorService;
	@Getter
	@Setter
	private Map<String, Object> handlerMap = new ConcurrentHashMap<>();
	private int numberOfEchoThreadsPool = 1;

	ThreadFactory threadRpcFactory = new NamedThreadFactory("NettyRPC ThreadFactory");
	EventLoopGroup boss = new NioEventLoopGroup();
	EventLoopGroup worker = new NioEventLoopGroup(PARALLEL, threadRpcFactory, SelectorProvider.provider());

	private MessageRecvExecutor() {
		handlerMap.clear();
		register();
	}

	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	private static class MessageRecvExecutorHolder {
		static final MessageRecvExecutor INSTANCE = new MessageRecvExecutor();
	}

	public static MessageRecvExecutor getInstance() {
		return MessageRecvExecutorHolder.INSTANCE;
	}

	public static void submit(Callable<Boolean> task, ChannelHandlerContext ctx, MessageRequest request, MessageResponse response) {
		if (executorService == null)
			synchronized (MessageRecvExecutor.class) {
				if (executorService == null)
					executorService = MoreExecutors.listeningDecorator((isMonitorServerSupport() ? getExecutorWithJmx(threadNums, queueNums) : getExecutor(threadNums, queueNums)));
			}

		ListenableFuture<Boolean> listenableFuture = executorService.submit(task);
		Futures.addCallback(listenableFuture, new FutureCallback<Boolean>() {
			@Override
			public void onSuccess(Boolean result) {
				ctx.writeAndFlush(response).addListener(new ChannelFutureListener() {
					@Override
					public void operationComplete(ChannelFuture channelFuture) throws Exception {
						log.info("RPC Server Send message-id respone:{}", request.getMessageId());
					}
				});
			}

			@Override
			public void onFailure(Throwable t) {
				log.error(t.getMessage(), t);
			}
		}, executorService);
	}

	@Override
	public void setApplicationContext(ApplicationContext ctx) {
		try {
			MessageKeyVal keyVal = (MessageKeyVal) ctx.getBean(Class.forName("com.newlandframework.rpc.model.MessageKeyVal"));
			Map<String, Object> rpcServiceObject = keyVal.getMessageMap();
			handlerMap.putAll(rpcServiceObject);
		} catch (ClassNotFoundException e) {
			log.error(e.getMessage(), e);
		}
	}

	public void start() throws InterruptedException {
		ServerBootstrap bootstrap = new ServerBootstrap();
		bootstrap.group(boss, worker).channel(NioServerSocketChannel.class).option(SO_BACKLOG, 128).childOption(SO_KEEPALIVE, true);
		bootstrap.childHandler(new MessageRecvChannelInitializer(handlerMap).buildRpcSerializeProtocol(serializeProtocol));

		String[] ipAddr = serverAddress.split(DELIMITER);

		if (ipAddr.length != IPADDR_OPRT_ARRAY_LENGTH) {
			log.error("Netty RPC Server start fail!");
			return;
		}

		final String host = ipAddr[0];
		final int port = Integer.parseInt(ipAddr[1]);
		ChannelFuture future = null;
		future = bootstrap.bind(host, port).sync();

		future.addListener(new ChannelFutureListener() {
			@Override
			public void operationComplete(final ChannelFuture channelFuture) throws Exception {
				if (channelFuture.isSuccess()) {
					final ExecutorService executor = Executors.newFixedThreadPool(numberOfEchoThreadsPool);
					ExecutorCompletionService<Boolean> completionService = new ExecutorCompletionService<>(executor);
					completionService.submit(new ApiEchoResolver(host, echoApiPort));
					log.info("Netty RPC Server start success!");
					log.info("ip:{}", host);
					log.info("port:{}", port);
					log.info("protocol:{}", serializeProtocol);
					log.info("start-time:{}", ModuleMetricsHandler.getStartTime());
					log.info("jmx-invoke-metrics:{}", SYSTEM_PROPERTY_JMX_METRICS_SUPPORT ? "open" : "close");
					channelFuture.channel().closeFuture().addListener(f -> executor.shutdownNow());
				}
			}
		});
	}

	public void stop() {
		worker.shutdownGracefully();
		boss.shutdownGracefully();
	}

	private void register() {
		handlerMap.put(RPC_COMPILER_SPI_ATTR, new AccessAdaptiveProvider());
		handlerMap.put(RPC_ABILITY_DETAIL_SPI_ATTR, new AbilityDetailProvider());
	}
}
