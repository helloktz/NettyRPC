package com.newlandframework.rpc.parallel.policy;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class DiscardedPolicy implements RejectedExecutionHandler {
	private String threadName;

	public DiscardedPolicy() {
		this(null);
	}

	public DiscardedPolicy(String threadName) {
		this.threadName = threadName;
	}

	@Override
	public void rejectedExecution(Runnable runnable, ThreadPoolExecutor executor) {
		if (threadName != null) {
			log.error("RPC Thread pool [{}] is exhausted, executor={}", threadName, executor.toString());
		}

		if (!executor.isShutdown()) {
			BlockingQueue<Runnable> queue = executor.getQueue();
			int discardSize = queue.size() >> 1;
			for (int i = 0; i < discardSize; i++) {
				queue.poll();
			}

			queue.offer(runnable);
		}
	}
}
