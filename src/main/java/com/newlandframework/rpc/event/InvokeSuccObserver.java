package com.newlandframework.rpc.event;

import java.util.Observable;

import com.newlandframework.rpc.jmx.ModuleMetricsVisitor;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InvokeSuccObserver extends AbstractInvokeObserver {
	private long invokeTimespan;

	public InvokeSuccObserver(InvokeEventBusFacade facade, ModuleMetricsVisitor visitor, long invokeTimespan) {
		super(facade, visitor);
		this.invokeTimespan = invokeTimespan;
	}

	@Override
	public void update(Observable o, Object arg) {
		if ((AbstractInvokeEventBus.ModuleEvent) arg == AbstractInvokeEventBus.ModuleEvent.INVOKE_SUCC_EVENT) {
			super.getFacade().fetchEvent(AbstractInvokeEventBus.ModuleEvent.INVOKE_SUCC_EVENT).notify(super.getVisitor().getInvokeSuccCount(), super.getVisitor().incrementInvokeSuccCount());
			super.getFacade().fetchEvent(AbstractInvokeEventBus.ModuleEvent.INVOKE_TIMESPAN_EVENT).notify(super.getVisitor().getInvokeTimespan(), invokeTimespan);
			super.getFacade().fetchEvent(AbstractInvokeEventBus.ModuleEvent.INVOKE_MAX_TIMESPAN_EVENT).notify(super.getVisitor().getInvokeMaxTimespan(), invokeTimespan);
			super.getFacade().fetchEvent(AbstractInvokeEventBus.ModuleEvent.INVOKE_MIN_TIMESPAN_EVENT).notify(super.getVisitor().getInvokeMinTimespan(), invokeTimespan);
		}
	}
}
