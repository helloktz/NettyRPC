package com.newlandframework.rpc.parallel.policy;

import java.util.concurrent.ThreadPoolExecutor;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class CallerRunsPolicy extends ThreadPoolExecutor.CallerRunsPolicy {
	private String threadName;

	public CallerRunsPolicy() {
		this(null);
	}

	public CallerRunsPolicy(String threadName) {
		this.threadName = threadName;
	}

	@Override
	public void rejectedExecution(Runnable runnable, ThreadPoolExecutor executor) {
		if (threadName != null) {
			log.error("RPC Thread pool [{}] is exhausted, executor={}", threadName, executor.toString());
		}

		super.rejectedExecution(runnable, executor);
	}
}
