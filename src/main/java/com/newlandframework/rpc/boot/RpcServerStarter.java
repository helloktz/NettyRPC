package com.newlandframework.rpc.boot;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class RpcServerStarter {
	@SuppressWarnings("resource")
	public static void main(String[] args) {
		new ClassPathXmlApplicationContext("classpath:rpc-invoke-config-server.xml");
	}
}
