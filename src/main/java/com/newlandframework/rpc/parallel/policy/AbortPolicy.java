package com.newlandframework.rpc.parallel.policy;

import java.util.concurrent.ThreadPoolExecutor;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class AbortPolicy extends ThreadPoolExecutor.AbortPolicy {
	private String threadName;

	public AbortPolicy() {
		this(null);
	}

	public AbortPolicy(String threadName) {
		this.threadName = threadName;
	}

	@Override
	public void rejectedExecution(Runnable runnable, ThreadPoolExecutor executor) {
		if (threadName != null) {
			log.error("RPC Thread pool [{}] is exhausted, executor={}", threadName, executor.toString());
		}
		String msg = String.format(
				"RpcServer[" + " Thread Name: %s, Pool Size: %d (active: %d, core: %d, max: %d, largest: %d), Task: %d (completed: %d),"
						+ " Executor status:(isShutdown:%s, isTerminated:%s, isTerminating:%s)]",
				threadName, executor.getPoolSize(), executor.getActiveCount(), executor.getCorePoolSize(), executor.getMaximumPoolSize(), executor.getLargestPoolSize(), executor.getTaskCount(),
				executor.getCompletedTaskCount(), executor.isShutdown(), executor.isTerminated(), executor.isTerminating());
		System.out.println(msg);
		super.rejectedExecution(runnable, executor);
	}
}
