package com.newlandframework.rpc.netty;

import java.net.InetSocketAddress;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.lang3.BooleanUtils;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.newlandframework.rpc.core.RpcSystemConfig;
import com.newlandframework.rpc.parallel.RpcThreadPool;
import com.newlandframework.rpc.serialize.RpcSerializeProtocol;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RpcServerLoader {
	private static final String DELIMITER = RpcSystemConfig.DELIMITER;
	@Setter
	private RpcSerializeProtocol serializeProtocol = RpcSerializeProtocol.JDKSERIALIZE;
	private static final int PARALLEL = RpcSystemConfig.SYSTEM_PROPERTY_PARALLEL * 2;
	private EventLoopGroup eventLoopGroup = new NioEventLoopGroup(PARALLEL);
	private static int threadNums = RpcSystemConfig.SYSTEM_PROPERTY_THREADPOOL_THREAD_NUMS;
	private static int queueNums = RpcSystemConfig.SYSTEM_PROPERTY_THREADPOOL_QUEUE_NUMS;
	private static ListeningExecutorService threadPoolExecutor = MoreExecutors.listeningDecorator(RpcThreadPool.getExecutor(threadNums, queueNums));
	private MessageSendHandler messageSendHandler = null;
	private Lock lock = new ReentrantLock();
	private Condition connectStatus = lock.newCondition();
	private Condition handlerStatus = lock.newCondition();

	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	private static final class RpcServerLoaderHolder {
		private static final RpcServerLoader INSTANCE = new RpcServerLoader();
	}

	public static RpcServerLoader getInstance() {
		return RpcServerLoaderHolder.INSTANCE;
	}

	public void load(String serverAddress, RpcSerializeProtocol serializeProtocol) {
		String[] ipAddr = serverAddress.split(RpcServerLoader.DELIMITER);
		if (ipAddr.length == RpcSystemConfig.IPADDR_OPRT_ARRAY_LENGTH) {
			String host = ipAddr[0];
			int port = Integer.parseInt(ipAddr[1]);
			final InetSocketAddress remoteAddr = new InetSocketAddress(host, port);

			log.info("Netty RPC Client start success!");
			log.info("ip:{}", host);
			log.info("port:{}", port);
			log.info("protocol:{}", serializeProtocol);

			ListenableFuture<Boolean> listenableFuture = threadPoolExecutor.submit(new MessageSendInitializeTask(eventLoopGroup, remoteAddr, serializeProtocol));

			Futures.addCallback(listenableFuture, new FutureCallback<Boolean>() {
				@Override
				public void onSuccess(Boolean result) {
					try {
						lock.lock();

						if (messageSendHandler == null)
							handlerStatus.await();

						if (BooleanUtils.isTrue(result) && messageSendHandler != null)
							connectStatus.signalAll();

					} catch (InterruptedException ex) {
						log.error(ex.getMessage(), ex);
						Thread.currentThread().interrupt();
					} finally {
						lock.unlock();
					}
				}

				@Override
				public void onFailure(Throwable t) {
					log.error(t.getMessage(), t);
				}
			}, threadPoolExecutor);
		}
	}

	public void setMessageSendHandler(MessageSendHandler messageInHandler) {
		try {
			lock.lock();
			this.messageSendHandler = messageInHandler;
			handlerStatus.signal();
		} finally {
			lock.unlock();
		}
	}

	public MessageSendHandler getMessageSendHandler() throws InterruptedException {
		try {
			lock.lock();
			if (messageSendHandler == null)
				connectStatus.await();

			return messageSendHandler;
		} finally {
			lock.unlock();
		}
	}

	public void unLoad() {
		if (messageSendHandler != null)
			messageSendHandler.close();

		threadPoolExecutor.shutdown();
		eventLoopGroup.shutdownGracefully();
	}
}
