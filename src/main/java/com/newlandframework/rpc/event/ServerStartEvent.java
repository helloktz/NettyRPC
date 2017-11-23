package com.newlandframework.rpc.event;

import org.springframework.context.ApplicationEvent;

public class ServerStartEvent extends ApplicationEvent {
	private static final long serialVersionUID = 1L;

	public ServerStartEvent(Object source) {
		super(source);
	}
}
