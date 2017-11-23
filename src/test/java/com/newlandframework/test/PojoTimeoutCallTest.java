package com.newlandframework.test;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.newlandframework.rpc.exception.InvokeTimeoutException;
import com.newlandframework.rpc.services.PersonManage;

public class PojoTimeoutCallTest {
	public static void main(String[] args) {
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("classpath:rpc-invoke-config-client.xml");

		PersonManage manage = (PersonManage) context.getBean("personManage");

		// TODO: 2017/10/28 by tangjie
		// NettyRPC default timeout is 30s.you can define it by
		// nettyrpc.default.msg.timeout environment variable.
		// if rpc call timeout,NettyRPC can throw InvokeTimeoutException.
		try {
			long timeout = 32L;
			manage.query(timeout);
		} catch (InvokeTimeoutException e) {
			// log.error(e);
			System.out.println(e.getMessage());
		} finally {
			context.destroy();
		}
	}
}
