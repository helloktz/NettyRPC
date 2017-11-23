package com.newlandframework.test.jdbc;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class NettyRpcJdbcServerTest {
	// FIXME: 2017/9/25
	// 确保先启动NettyRPC服务端应用:NettyRpcJdbcServerTest，再运行NettyRpcJdbcClientTest、NettyRpcJdbcClientErrorTest！
	public static void main(String[] args) {
		new ClassPathXmlApplicationContext("classpath:rpc-invoke-config-jdbc-server.xml");
	}
}
