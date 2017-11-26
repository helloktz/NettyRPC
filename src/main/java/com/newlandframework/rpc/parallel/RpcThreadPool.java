package com.newlandframework.rpc.parallel;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.management.JMException;

import com.newlandframework.rpc.core.RpcSystemConfig;
import com.newlandframework.rpc.jmx.ThreadPoolMonitorProvider;
import com.newlandframework.rpc.jmx.ThreadPoolStatus;
import com.newlandframework.rpc.parallel.policy.AbortPolicy;
import com.newlandframework.rpc.parallel.policy.BlockingPolicy;
import com.newlandframework.rpc.parallel.policy.CallerRunsPolicy;
import com.newlandframework.rpc.parallel.policy.DiscardedPolicy;
import com.newlandframework.rpc.parallel.policy.RejectedPolicy;
import com.newlandframework.rpc.parallel.policy.RejectedPolicyType;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RpcThreadPool {
	private static final Timer TIMER = new Timer("ThreadPoolMonitor", true);
	private static long monitorDelay = 100L;
	private static long monitorPeriod = 300L;

	private static RejectedExecutionHandler createPolicy() {
		RejectedPolicyType rejectedPolicyType = RejectedPolicyType.fromString(System.getProperty(RpcSystemConfig.SYSTEM_PROPERTY_THREADPOOL_REJECTED_POLICY_ATTR, "AbortPolicy"));

		switch (rejectedPolicyType) {
		case BLOCKING_POLICY:
			return new BlockingPolicy();
		case CALLER_RUNS_POLICY:
			return new CallerRunsPolicy();
		case ABORT_POLICY:
			return new AbortPolicy();
		case REJECTED_POLICY:
			return new RejectedPolicy();
		case DISCARDED_POLICY:
			return new DiscardedPolicy();
		}

		return null;
	}

	private static BlockingQueue<Runnable> createBlockingQueue(int queues) {
		BlockingQueueType queueType = BlockingQueueType.fromString(System.getProperty(RpcSystemConfig.SYSTEM_PROPERTY_THREADPOOL_QUEUE_NAME_ATTR, "LinkedBlockingQueue"));

		switch (queueType) {
		case LINKED_BLOCKING_QUEUE:
			return new LinkedBlockingQueue<>();
		case ARRAY_BLOCKING_QUEUE:
			return new ArrayBlockingQueue<>(RpcSystemConfig.SYSTEM_PROPERTY_PARALLEL * queues);
		case SYNCHRONOUS_QUEUE:
			return new SynchronousQueue<>();
		default:
			throw new NullPointerException();
		}
	}

	public static ThreadPoolExecutor getExecutor(int threads, int queues) {
		log.info("ThreadPool Core[threads:{}, queues:{}]", threads, queues);
		String name = "RpcThreadPool";
		return new ThreadPoolExecutor(threads, threads, 0, TimeUnit.MILLISECONDS, createBlockingQueue(queues), new NamedThreadFactory(name, true), createPolicy());
	}

	public static ThreadPoolExecutor getExecutorWithJmx(int threads, int queues) {
		ThreadPoolExecutor executor = getExecutor(threads, queues);
		TIMER.scheduleAtFixedRate(new TimerTask() {

			@Override
			public void run() {
				ThreadPoolStatus status = new ThreadPoolStatus();
				status.setPoolSize(executor.getPoolSize());
				status.setActiveCount(executor.getActiveCount());
				status.setCorePoolSize(executor.getCorePoolSize());
				status.setMaximumPoolSize(executor.getMaximumPoolSize());
				status.setLargestPoolSize(executor.getLargestPoolSize());
				status.setTaskCount(executor.getTaskCount());
				status.setCompletedTaskCount(executor.getCompletedTaskCount());

				try {
					ThreadPoolMonitorProvider.monitor(status);
				} catch (JMException | IOException e) {
					log.error(e.getMessage(), e);
				}
			}
		}, monitorDelay, monitorDelay);
		return executor;
	}
}
