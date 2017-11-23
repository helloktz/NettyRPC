package com.newlandframework.rpc.event;

import java.util.Observable;

import com.newlandframework.rpc.jmx.ModuleMetricsVisitor;

public class InvokeFilterObserver extends AbstractInvokeObserver {

	public InvokeFilterObserver(InvokeEventBusFacade facade, ModuleMetricsVisitor visitor) {
		super(facade, visitor);
	}

	@Override
	public void update(Observable o, Object arg) {
		if ((AbstractInvokeEventBus.ModuleEvent) arg == AbstractInvokeEventBus.ModuleEvent.INVOKE_FILTER_EVENT) {
			super.getFacade().fetchEvent(AbstractInvokeEventBus.ModuleEvent.INVOKE_FILTER_EVENT).notify(super.getVisitor().getInvokeFilterCount(), super.getVisitor().incrementInvokeFilterCount());
		}
	}
}
