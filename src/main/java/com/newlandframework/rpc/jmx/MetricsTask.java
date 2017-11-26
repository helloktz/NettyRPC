package com.newlandframework.rpc.jmx;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MetricsTask implements Runnable {
	private final CyclicBarrier barrier;
	private List<ModuleMetricsVisitor> visitorList;
	@Getter
	@Setter
	private List<ModuleMetricsVisitor> result = new ArrayList<>();

	public MetricsTask(CyclicBarrier barrier, List<ModuleMetricsVisitor> visitorList) {
		this.barrier = barrier;
		this.visitorList = visitorList;
	}

	@Override
	public void run() {
		try {
			barrier.await();
			accumulate();
			barrier.await();
		} catch (InterruptedException | BrokenBarrierException e) {
			log.error(e.getMessage(), e);
		}
	}

	private void count(List<ModuleMetricsVisitor> list) {
		for (int i = 0; i < result.size(); i++) {
			long invokeCount = 0L;
			long invokeSuccCount = 0L;
			long invokeFailCount = 0L;
			long invokeFilterCount = 0L;
			long invokeTimespan = 0L;
			long invokeMinTimespan = list.get(0).getInvokeMinTimespan();
			long invokeMaxTimespan = list.get(0).getInvokeMaxTimespan();
			int length = result.get(i).getHistogram().getRanges().length + 1;
			long[] invokeHistogram = new long[length];
			Arrays.fill(invokeHistogram, 0L);
			String lastStackTraceDetail = "";
			long lastErrorTime = list.get(0).getErrorLastTimeLongVal();

			ModuleMetrics metrics = new ModuleMetrics();
			metrics.setInvokeCount(invokeCount);
			metrics.setInvokeSuccCount(invokeSuccCount);
			metrics.setInvokeFailCount(invokeFailCount);
			metrics.setInvokeFilterCount(invokeFilterCount);
			metrics.setInvokeTimespan(invokeTimespan);
			metrics.setInvokeMinTimespan(invokeMinTimespan);
			metrics.setInvokeMaxTimespan(invokeMaxTimespan);
			metrics.setInvokeHistogram(invokeHistogram);
			metrics.setLastStackTraceDetail(lastStackTraceDetail);
			metrics.setLastErrorTime(lastErrorTime);

			merge(i, list, metrics);

			result.get(i).setInvokeCount(metrics.getInvokeCount());
			result.get(i).setInvokeSuccCount(metrics.getInvokeSuccCount());
			result.get(i).setInvokeFailCount(metrics.getInvokeFailCount());
			result.get(i).setInvokeFilterCount(metrics.getInvokeFilterCount());
			result.get(i).setInvokeTimespan(metrics.getInvokeTimespan());
			result.get(i).setInvokeMaxTimespan(metrics.getInvokeMaxTimespan());
			result.get(i).setInvokeMinTimespan(metrics.getInvokeMinTimespan());
			result.get(i).setInvokeHistogram(metrics.getInvokeHistogram());

			if (metrics.getLastErrorTime() > 0) {
				result.get(i).setErrorLastTimeLongVal(metrics.getLastErrorTime());
				result.get(i).setLastStackTraceDetail(metrics.getLastStackTraceDetail());
			}
		}
	}

	private void merge(int index, List<ModuleMetricsVisitor> list, ModuleMetrics metrics) {
		long invokeCount = metrics.getInvokeCount();
		long invokeSuccCount = metrics.getInvokeSuccCount();
		long invokeFailCount = metrics.getInvokeFailCount();
		long invokeFilterCount = metrics.getInvokeFilterCount();
		long invokeTimespan = metrics.getInvokeTimespan();
		long invokeMinTimespan = metrics.getInvokeMinTimespan();
		long invokeMaxTimespan = metrics.getInvokeMaxTimespan();
		long[] invokeHistogram = metrics.getInvokeHistogram();
		String lastStackTraceDetail = metrics.getLastStackTraceDetail();
		long lastErrorTime = metrics.getLastErrorTime();

		for (int i = 0; i < list.size(); i++) {
			boolean find = equals(result.get(index).getModuleName(), list.get(i).getModuleName(), result.get(index).getMethodName(), list.get(i).getMethodName());
			if (find) {
				invokeCount += list.get(i).getInvokeCount();
				invokeSuccCount += list.get(i).getInvokeSuccCount();
				invokeFailCount += list.get(i).getInvokeFailCount();
				invokeFilterCount += list.get(i).getInvokeFilterCount();
				long timespan = list.get(i).getInvokeTimespan();
				if (timespan > 0) {
					invokeTimespan = timespan;
				}
				long minTimespan = list.get(i).getInvokeMinTimespan();
				long maxTimespan = list.get(i).getInvokeMaxTimespan();
				if (minTimespan < invokeMinTimespan) {
					invokeMinTimespan = minTimespan;
				}
				if (maxTimespan > invokeMaxTimespan) {
					invokeMaxTimespan = maxTimespan;
				}

				for (int j = 0; j < invokeHistogram.length; j++) {
					invokeHistogram[j] += list.get(i).getHistogram().toArray()[j];
				}

				long fail = list.get(i).getInvokeFailCount();
				if (fail > 0) {
					long lastTime = list.get(i).getErrorLastTimeLongVal();
					if (lastTime > lastErrorTime) {
						lastErrorTime = lastTime;
						lastStackTraceDetail = list.get(i).getLastStackTraceDetail();
					}
				}
			}
		}

		metrics.setInvokeCount(invokeCount);
		metrics.setInvokeSuccCount(invokeSuccCount);
		metrics.setInvokeFailCount(invokeFailCount);
		metrics.setInvokeFilterCount(invokeFilterCount);
		metrics.setInvokeTimespan(invokeTimespan);
		metrics.setInvokeMinTimespan(invokeMinTimespan);
		metrics.setInvokeMaxTimespan(invokeMaxTimespan);
		metrics.setInvokeHistogram(invokeHistogram);
		metrics.setLastStackTraceDetail(lastStackTraceDetail);
		metrics.setLastErrorTime(lastErrorTime);
	}

	private void accumulate() {
		visitorList.stream().distinct().map(v -> new ModuleMetricsVisitor(v.getModuleName(), v.getMethodName())).forEach(result::add);
		count(visitorList);
	}

	private boolean equals(String srcModuleName, String destModuleName, String srcMethodName, String destMethodName) {
		return srcModuleName.equals(destModuleName) && srcMethodName.equals(destMethodName);
	}

	@Data
	private class ModuleMetrics {
		private long invokeCount;
		private long invokeSuccCount;
		private long invokeFailCount;
		private long invokeFilterCount;
		private long invokeTimespan;
		private long invokeMinTimespan;
		private long invokeMaxTimespan;
		private long[] invokeHistogram;
		private String lastStackTraceDetail;
		private long lastErrorTime;
	}
}
