package com.newlandframework.rpc.event;

import java.util.concurrent.atomic.AtomicLong;

import javax.management.AttributeChangeNotification;
import javax.management.Notification;

public class InvokeMinTimeSpanEvent extends AbstractInvokeEventBus {
	private AtomicLong sequenceInvokeMinTimeSpanNumber = new AtomicLong(0L);

	public InvokeMinTimeSpanEvent() {
		super();
	}

	public InvokeMinTimeSpanEvent(String moduleName, String methodName) {
		super(moduleName, methodName);
	}

	@Override
	public Notification buildNotification(Object oldValue, Object newValue) {
		return new AttributeChangeNotification(this, sequenceInvokeMinTimeSpanNumber.incrementAndGet(), System.currentTimeMillis(), super.moduleName, super.methodName,
				ModuleEvent.INVOKE_MIN_TIMESPAN_EVENT.toString(), oldValue, newValue);
	}
}
