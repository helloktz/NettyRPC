package com.newlandframework.rpc.event;

import java.util.concurrent.atomic.AtomicLong;

import javax.management.AttributeChangeNotification;
import javax.management.Notification;

public class InvokeEvent extends AbstractInvokeEventBus {
	private AtomicLong sequenceInvokeNumber = new AtomicLong(0L);

	public InvokeEvent() {
		super();
	}

	public InvokeEvent(String moduleName, String methodName) {
		super(moduleName, methodName);
	}

	@Override
	public Notification buildNotification(Object oldValue, Object newValue) {
		return new AttributeChangeNotification(this, sequenceInvokeNumber.incrementAndGet(), System.currentTimeMillis(), super.moduleName, super.methodName, ModuleEvent.INVOKE_EVENT.toString(),
				oldValue, newValue);
	}
}
