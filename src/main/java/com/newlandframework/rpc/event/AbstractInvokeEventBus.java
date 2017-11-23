package com.newlandframework.rpc.event;

import javax.management.Notification;

import com.newlandframework.rpc.jmx.ModuleMetricsHandler;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public abstract class AbstractInvokeEventBus {
	public enum ModuleEvent {
		INVOKE_EVENT, INVOKE_SUCC_EVENT, INVOKE_TIMESPAN_EVENT, INVOKE_MAX_TIMESPAN_EVENT, INVOKE_MIN_TIMESPAN_EVENT, INVOKE_FILTER_EVENT, INVOKE_FAIL_EVENT, INVOKE_FAIL_STACKTRACE_EVENT
	}

	protected String moduleName;
	protected String methodName;
	protected ModuleMetricsHandler handler;

	public AbstractInvokeEventBus(String moduleName, String methodName) {
		this.moduleName = moduleName;
		this.methodName = methodName;
	}

	public abstract Notification buildNotification(Object oldValue, Object newValue);

	public void notify(Object oldValue, Object newValue) {
		Notification notification = buildNotification(oldValue, newValue);
		handler.sendNotification(notification);
	}
}
