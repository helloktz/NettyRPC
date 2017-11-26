package com.newlandframework.test;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import com.newlandframework.rpc.exception.InvokeTimeoutException;
import com.newlandframework.rpc.services.PersonManage;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RunWith(SpringRunner.class)
@ContextConfiguration("classpath:rpc-invoke-config-client.xml")
public class PojoTimeoutCallTest {

	@Resource(name = "personManage")
	private PersonManage manage;

	@Test
	public void testPojoTimeoutCall() {
		// NettyRPC default timeout is 30s.you can define it by
		// nettyrpc.default.msg.timeout environment variable.
		// if rpc call timeout,NettyRPC can throw InvokeTimeoutException.
		try {
			long timeout = 32L;
			manage.query(timeout);
		} catch (InvokeTimeoutException e) {
			log.error(e.getMessage(), e);
		}
	}
}
