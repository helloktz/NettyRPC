package com.newlandframework.rpc.event;

import lombok.Getter;

@Getter
public class ClientStopEvent {
	private final int message;

	public ClientStopEvent(int message) {
		this.message = message;
	}
}
