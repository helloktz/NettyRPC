package com.newlandframework.rpc.jmx;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import javax.management.JMException;
import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.openmbean.CompositeData;
import javax.management.openmbean.CompositeDataSupport;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ModuleMetricsProcessor {
	private static final ModuleMetricsProcessor INSTANCE = new ModuleMetricsProcessor();
	private MBeanServerConnection connection;
	private static final String TD_BEGIN = "<td>";
	private static final String TD_END = "</td>";
	private static final String TR_BEGIN = "<tr>";
	private static final String TR_END = "</tr>";
	private static final String BR = "</br>";
	private static final String TABLE_BEGIN = "<html><body><div class=\"table-container\"><table border=\"1\"><tr><th>模块名称</th><th>方法名称</th><th>调用次数</th><th>调用成功次数</th><th>调用失败次数</th><th>被过滤次数</th><th>方法耗时（毫秒）</th><th>方法最大耗时（毫秒）</th><th>方法最小耗时（毫秒）</th><th>方法耗时区间分布</th><th>最后一次失败时间</th><th>最后一次失败堆栈明细</th></tr>";
	private static final String TABLE_END = "</table></body></html>";
	private static final String SUB_TABLE_BEGIN = "<table border=\"1\">";
	private static final String SUB_TABLE_END = "</table>";
	private static final String JMX_METRICS_ATTR = "ModuleMetricsVisitor";

	public static ModuleMetricsProcessor getInstance() {
		return INSTANCE;
	}

	private ModuleMetricsProcessor() {
		init();
	}

	private void init() {
		ModuleMetricsHandler handler = ModuleMetricsHandler.getInstance();
		connection = handler.connect();

		while (true) {
			if (connection != null) {
				break;
			} else {
				try {
					TimeUnit.SECONDS.sleep(1L);
					connection = handler.connect();
				} catch (InterruptedException e) {
					log.error(e.getMessage(), e);
					Thread.currentThread().interrupt();
				}
			}
		}
	}

	private String buildHistogram(CompositeData data) {
		CompositeDataSupport histogram = (CompositeDataSupport) (data.get("histogram"));
		long[] ranges = (long[]) (histogram.get("ranges"));
		long[] invokeHistogram = (long[]) (data.get("invokeHistogram"));
		StringBuilder distribute = new StringBuilder();
		distribute.append(SUB_TABLE_BEGIN);
		int i = 0;
		for (; i < ranges.length; i++) {
			distribute.append(TR_BEGIN + TD_BEGIN + ((i == 0) ? i : ranges[i - 1]) + "~" + ranges[i] + "毫秒的笔数：" + invokeHistogram[i] + BR + TD_END + TR_END);
		}
		distribute.append(TR_BEGIN + TD_BEGIN + "大于等于" + ranges[i - 1] + "毫秒的笔数：" + invokeHistogram[i] + BR + TD_END + TR_END);
		distribute.append(SUB_TABLE_END);
		return distribute.toString();
	}

	public String buildModuleMetrics() {
		StringBuilder metrics = new StringBuilder();

		metrics.append(TABLE_BEGIN);
		ObjectName name = null;
		try {
			name = new ObjectName(ModuleMetricsHandler.MBEAN_NAME);
		} catch (MalformedObjectNameException e) {
			log.error(e.getMessage(), e);
		}

		try {
			Object obj = connection.getAttribute(name, JMX_METRICS_ATTR);
			if (obj instanceof CompositeData[]) {
				for (CompositeData compositeData : (CompositeData[]) obj) {
					CompositeData data = compositeData;
					String moduleName = (String) (data.get("moduleName"));
					String methodName = (String) (data.get("methodName"));
					long invokeCount = (Long) (data.get("invokeCount"));
					long invokeSuccCount = (Long) (data.get("invokeSuccCount"));
					long invokeFailCount = (Long) (data.get("invokeFailCount"));
					long invokeFilterCount = (Long) (data.get("invokeFilterCount"));
					long invokeTimespan = (Long) (data.get("invokeTimespan"));
					long invokeMinTimespan = ((Long) (data.get("invokeMinTimespan"))).equals(Long.valueOf(ModuleMetricsVisitor.DEFAULT_INVOKE_MIN_TIMESPAN)) ? Long.valueOf(0L)
							: (Long) (data.get("invokeMinTimespan"));
					long invokeMaxTimespan = (Long) (data.get("invokeMaxTimespan"));
					String lastStackTraceDetail = (String) (data.get("lastStackTraceDetail"));
					String lastErrorTime = (String) (data.get("lastErrorTime"));
					String distribute = buildHistogram(data);
					metrics.append(TR_BEGIN);
					metrics.append(TD_BEGIN + moduleName + TD_END);
					metrics.append(TD_BEGIN + methodName + TD_END);
					metrics.append(TD_BEGIN + invokeCount + TD_END);
					metrics.append(TD_BEGIN + invokeSuccCount + TD_END);
					metrics.append(TD_BEGIN + invokeFailCount + TD_END);
					metrics.append(TD_BEGIN + invokeFilterCount + TD_END);
					metrics.append(TD_BEGIN + invokeTimespan + TD_END);
					metrics.append(TD_BEGIN + invokeMaxTimespan + TD_END);
					metrics.append(TD_BEGIN + invokeMinTimespan + TD_END);
					metrics.append(TD_BEGIN + distribute + TD_END);
					metrics.append(TD_BEGIN + (lastErrorTime != null ? lastErrorTime : "") + TD_END);
					metrics.append(TD_BEGIN + lastStackTraceDetail + TD_END);
					metrics.append(TR_END);
				}
			}
			metrics.append(TABLE_END);
		} catch (JMException | IOException e) {
			log.error(e.getMessage(), e);
		}

		return metrics.toString();
	}
}
