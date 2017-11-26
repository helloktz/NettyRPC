package com.newlandframework.test;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import com.newlandframework.rpc.async.AsyncCallObject;
import com.newlandframework.rpc.async.AsyncCallback;
import com.newlandframework.rpc.async.AsyncInvoker;
import com.newlandframework.rpc.services.CostTimeCalculate;
import com.newlandframework.rpc.services.pojo.CostTime;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RunWith(SpringRunner.class)
@ContextConfiguration("classpath:rpc-invoke-config-client.xml")
public class AsyncRpcCallErrorTest {

	@Resource(name = "costTime")
	private CostTimeCalculate calculate;

	@Test
	public void testAsyncRpcCallError() {
		AsyncInvoker invoker = new AsyncInvoker();
		CostTime elapse0 = invoker.submit(new AsyncCallback<CostTime>() {
			@Override
			public CostTime call() {
				throw new RuntimeException("calculate fail 1!");
			}
		});

		log.info("1 async nettyrpc call:[result:{}], status:[{}]", elapse0, ((AsyncCallObject) elapse0)._getStatus());
	}
}
