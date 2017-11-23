package com.newlandframework.rpc.event;

import java.util.concurrent.atomic.AtomicLong;

import javax.management.AttributeChangeNotification;
import javax.management.Notification;

public class InvokeFilterEvent extends AbstractInvokeEventBus {
	private AtomicLong sequenceInvokeFilterNumber = new AtomicLong(0L);

	public InvokeFilterEvent() {
		super();
	}

	public InvokeFilterEvent(String moduleName, String methodName) {
		super(moduleName, methodName);
	}

	@Override
	public Notification buildNotification(Object oldValue, Object newValue) {
		return new AttributeChangeNotification(this, sequenceInvokeFilterNumber.incrementAndGet(), System.currentTimeMillis(), super.moduleName, super.methodName,
				ModuleEvent.INVOKE_FILTER_EVENT.toString(), oldValue, newValue);
	}
}
