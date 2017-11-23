package com.newlandframework.rpc.event;

import com.google.common.eventbus.Subscribe;
import com.newlandframework.rpc.netty.MessageSendExecutor;

import lombok.Getter;

public class ClientStopEventListener {
	@Getter
	public int lastMessage = 0;

	@Subscribe
	public void listen(ClientStopEvent event) {
		lastMessage = event.getMessage();
		MessageSendExecutor.getInstance().stop();
	}
}
