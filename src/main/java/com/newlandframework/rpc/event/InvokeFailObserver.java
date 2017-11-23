package com.newlandframework.rpc.event;

import java.util.Observable;

import com.newlandframework.rpc.jmx.ModuleMetricsVisitor;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InvokeFailObserver extends AbstractInvokeObserver {
	private Throwable error;

	public InvokeFailObserver(InvokeEventBusFacade facade, ModuleMetricsVisitor visitor, Throwable error) {
		super(facade, visitor);
		this.error = error;
	}

	@Override
	public void update(Observable o, Object arg) {
		if ((AbstractInvokeEventBus.ModuleEvent) arg == AbstractInvokeEventBus.ModuleEvent.INVOKE_FAIL_EVENT) {
			super.getFacade().fetchEvent(AbstractInvokeEventBus.ModuleEvent.INVOKE_FAIL_EVENT).notify(super.getVisitor().getInvokeFailCount(), super.getVisitor().incrementInvokeFailCount());
			super.getFacade().fetchEvent(AbstractInvokeEventBus.ModuleEvent.INVOKE_FAIL_STACKTRACE_EVENT).notify(null, error);
		}
	}
}
