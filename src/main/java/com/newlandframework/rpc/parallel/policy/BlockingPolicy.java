package com.newlandframework.rpc.parallel.policy;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BlockingPolicy implements RejectedExecutionHandler {
	private String threadName;

	public BlockingPolicy() {
		this(null);
	}

	public BlockingPolicy(String threadName) {
		this.threadName = threadName;
	}

	@Override
	public void rejectedExecution(Runnable runnable, ThreadPoolExecutor executor) {
		if (threadName != null) {
			log.error("RPC Thread pool [{}] is exhausted, executor={}", threadName, executor.toString());
		}

		if (!executor.isShutdown()) {
			try {
				executor.getQueue().put(runnable);
			} catch (InterruptedException e) {
				log.error(e.getMessage(), e);
				Thread.currentThread().interrupt();
			}
		}
	}
}
