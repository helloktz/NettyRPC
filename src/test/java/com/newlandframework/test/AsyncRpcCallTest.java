package com.newlandframework.test;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import com.newlandframework.rpc.async.AsyncCallObject;
import com.newlandframework.rpc.async.AsyncInvoker;
import com.newlandframework.rpc.services.CostTimeCalculate;
import com.newlandframework.rpc.services.pojo.CostTime;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RunWith(SpringRunner.class)
@ContextConfiguration("classpath:rpc-invoke-config-client.xml")
public class AsyncRpcCallTest {

	@Resource(name = "costTime")
	private CostTimeCalculate calculate;

	@Test
	public void testAsyncRpcCall() {
		long start = 0;
		long end = 0;
		start = System.currentTimeMillis();

		AsyncInvoker invoker = new AsyncInvoker();

		CostTime elapse1 = invoker.submit(() -> calculate.calculate());

		CostTime elapse2 = invoker.submit(() -> calculate.calculate());

		CostTime elapse3 = invoker.submit(() -> calculate.calculate());

		log.info("1 async nettyrpc call:[result:{}, status:[{}]]", elapse1, ((AsyncCallObject) elapse1)._getStatus());
		log.info("2 async nettyrpc call:[result:{}, status:[{}]]", elapse2, ((AsyncCallObject) elapse2)._getStatus());
		log.info("3 async nettyrpc call:[result:{}, status:[{}]]", elapse3, ((AsyncCallObject) elapse3)._getStatus());

		end = System.currentTimeMillis();

		log.info("nettyrpc async calculate time:", end - start);
	}
}
