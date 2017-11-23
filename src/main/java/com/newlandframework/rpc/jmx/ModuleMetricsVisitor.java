package com.newlandframework.rpc.jmx;

import java.beans.ConstructorProperties;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLongFieldUpdater;

import javax.management.JMException;
import javax.management.openmbean.CompositeData;
import javax.management.openmbean.CompositeDataSupport;
import javax.management.openmbean.CompositeType;
import javax.management.openmbean.OpenType;
import javax.management.openmbean.SimpleType;

import org.apache.commons.lang3.exception.ExceptionUtils;

import com.alibaba.druid.util.Histogram;
import com.newlandframework.rpc.core.RpcSystemConfig;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@EqualsAndHashCode(of = { "moduleName", "methodName" })
@ToString(of = { "moduleName", "methodName", "invokeCount", "invokeSuccCount", "invokeFilterCount", "invokeTimespan", "invokeMinTimespan", "invokeMaxTimespan", "invokeFailCount", "lastErrorTime", "lastStackTraceDetail" })
public class ModuleMetricsVisitor {
	public static final long DEFAULT_INVOKE_MIN_TIMESPAN = 3600 * 1000L;
	private static final String[] THROWABLE_NAMES = { "message", "class", "stackTrace" };
	private static final String[] THROWABLE_DESCRIPTIONS = { "message", "class", "stackTrace" };
	private static final OpenType<?>[] THROWABLE_TYPES = new OpenType<?>[] { SimpleType.STRING, SimpleType.STRING, SimpleType.STRING };
	private static CompositeType THROWABLE_COMPOSITE_TYPE = null;

	@Getter
	@Setter
	private String moduleName;
	@Getter
	@Setter
	private String methodName;
	@SuppressWarnings("unused")
	private volatile long invokeCount = 0L;
	@SuppressWarnings("unused")
	private volatile long invokeSuccCount = 0L;
	@SuppressWarnings("unused")
	private volatile long invokeFailCount = 0L;
	@SuppressWarnings("unused")
	private volatile long invokeFilterCount = 0L;
	@Getter
	@Setter
	private long invokeTimespan = 0L;
	@Getter
	@Setter
	private long invokeMinTimespan = DEFAULT_INVOKE_MIN_TIMESPAN;
	@Getter
	@Setter
	private long invokeMaxTimespan = 0L;
	@Setter
	private long[] invokeHistogram;
	@Getter
	private Exception lastStackTrace;
	@Getter
	@Setter
	private String lastStackTraceDetail;
	private long lastErrorTime;
	@Getter
	@Setter
	private int hashKey = 0;

	@Getter
	@Setter
	private Histogram histogram = new Histogram(TimeUnit.MILLISECONDS, new long[] { 1, 10, 100, 1000, 10 * 1000, 100 * 1000, 1000 * 1000 });

	private final AtomicLongFieldUpdater<ModuleMetricsVisitor> invokeCountUpdater = AtomicLongFieldUpdater.newUpdater(ModuleMetricsVisitor.class, "invokeCount");
	private final AtomicLongFieldUpdater<ModuleMetricsVisitor> invokeSuccCountUpdater = AtomicLongFieldUpdater.newUpdater(ModuleMetricsVisitor.class, "invokeSuccCount");
	private final AtomicLongFieldUpdater<ModuleMetricsVisitor> invokeFailCountUpdater = AtomicLongFieldUpdater.newUpdater(ModuleMetricsVisitor.class, "invokeFailCount");
	private final AtomicLongFieldUpdater<ModuleMetricsVisitor> invokeFilterCountUpdater = AtomicLongFieldUpdater.newUpdater(ModuleMetricsVisitor.class, "invokeFilterCount");

	@ConstructorProperties({ "moduleName", "methodName" })
	public ModuleMetricsVisitor(String moduleName, String methodName) {
		this.moduleName = moduleName;
		this.methodName = methodName;
		clear();
	}

	public void clear() {
		lastStackTraceDetail = "";
		invokeTimespan = 0L;
		invokeMinTimespan = DEFAULT_INVOKE_MIN_TIMESPAN;
		invokeMaxTimespan = 0L;
		lastErrorTime = 0L;
		lastStackTrace = null;
		invokeCountUpdater.set(this, 0);
		invokeSuccCountUpdater.set(this, 0);
		invokeFailCountUpdater.set(this, 0);
		invokeFilterCountUpdater.set(this, 0);
		histogram.reset();
	}

	public void reset() {
		moduleName = "";
		methodName = "";
		clear();
	}

	public void setErrorLastTimeLongVal(long lastErrorTime) {
		this.lastErrorTime = lastErrorTime;
	}

	public long getErrorLastTimeLongVal() {
		return lastErrorTime;
	}

	public String getLastErrorTime() {
		if (lastErrorTime <= 0) {
			return null;
		}
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return format.format(new Date(lastErrorTime));
	}

	public void setLastStackTrace(Exception lastStackTrace) {
		this.lastStackTrace = lastStackTrace;
		this.lastStackTraceDetail = lastStackTrace == null ? null : ExceptionUtils.getStackTrace(lastStackTrace);
		this.lastErrorTime = System.currentTimeMillis();
	}

	public CompositeType getThrowableCompositeType() throws JMException {
		if (THROWABLE_COMPOSITE_TYPE == null) {
			THROWABLE_COMPOSITE_TYPE = new CompositeType("Throwable", "Throwable", THROWABLE_NAMES, THROWABLE_DESCRIPTIONS, THROWABLE_TYPES);
		}

		return THROWABLE_COMPOSITE_TYPE;
	}

	public CompositeData buildErrorCompositeData(Throwable error) throws JMException {
		if (error == null) {
			return null;
		}

		Map<String, Object> map = new HashMap<String, Object>(512);

		map.put("class", error.getClass().getName());
		map.put("message", error.getMessage());
		map.put("stackTrace", ExceptionUtils.getStackTrace(error));

		return new CompositeDataSupport(getThrowableCompositeType(), map);
	}

	public long getInvokeCount() {
		return this.invokeCountUpdater.get(this);
	}

	public void setInvokeCount(long invokeCount) {
		this.invokeCountUpdater.set(this, invokeCount);
	}

	public long incrementInvokeCount() {
		return this.invokeCountUpdater.incrementAndGet(this);
	}

	public long getInvokeSuccCount() {
		return this.invokeSuccCountUpdater.get(this);
	}

	public void setInvokeSuccCount(long invokeSuccCount) {
		this.invokeSuccCountUpdater.set(this, invokeSuccCount);
	}

	public long incrementInvokeSuccCount() {
		return this.invokeSuccCountUpdater.incrementAndGet(this);
	}

	public long getInvokeFailCount() {
		return this.invokeFailCountUpdater.get(this);
	}

	public void setInvokeFailCount(long invokeFailCount) {
		this.invokeFailCountUpdater.set(this, invokeFailCount);
	}

	public long incrementInvokeFailCount() {
		return this.invokeFailCountUpdater.incrementAndGet(this);
	}

	public long getInvokeFilterCount() {
		return this.invokeFilterCountUpdater.get(this);
	}

	public void setInvokeFilterCount(long invokeFilterCount) {
		this.invokeFilterCountUpdater.set(this, invokeFilterCount);
	}

	public long incrementInvokeFilterCount() {
		return this.invokeFilterCountUpdater.incrementAndGet(this);
	}

	public long[] getInvokeHistogram() {
		return RpcSystemConfig.SYSTEM_PROPERTY_JMX_METRICS_HASH_SUPPORT ? invokeHistogram : histogram.toArray();
	}
}
