package com.newlandframework.rpc.event;

import java.util.Observer;

import com.newlandframework.rpc.jmx.ModuleMetricsVisitor;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class AbstractInvokeObserver implements Observer {
	private InvokeEventBusFacade facade;
	private ModuleMetricsVisitor visitor;

	public AbstractInvokeObserver(InvokeEventBusFacade facade, ModuleMetricsVisitor visitor) {
		this.facade = facade;
		this.visitor = visitor;
	}
}
