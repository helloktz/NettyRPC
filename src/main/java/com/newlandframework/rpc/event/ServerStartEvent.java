package com.newlandframework.rpc.event;

import org.springframework.context.ApplicationEvent;

@SuppressWarnings("serial")
public class ServerStartEvent extends ApplicationEvent {

	public ServerStartEvent(Object source) {
		super(source);
	}
}
