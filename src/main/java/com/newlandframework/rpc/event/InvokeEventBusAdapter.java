package com.newlandframework.rpc.event;

import javax.management.Notification;

public class InvokeEventBusAdapter extends AbstractInvokeEventBus {
	@Override
	public Notification buildNotification(Object oldValue, Object newValue) {
		return null;
	}
}
