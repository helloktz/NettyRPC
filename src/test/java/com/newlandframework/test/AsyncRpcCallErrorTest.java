package com.newlandframework.test;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.newlandframework.rpc.async.AsyncCallObject;
import com.newlandframework.rpc.async.AsyncCallback;
import com.newlandframework.rpc.async.AsyncInvoker;
import com.newlandframework.rpc.exception.AsyncCallException;
import com.newlandframework.rpc.services.CostTimeCalculate;
import com.newlandframework.rpc.services.pojo.CostTime;

public class AsyncRpcCallErrorTest {
	public static void main(String[] args) {
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("classpath:rpc-invoke-config-client.xml");

		final CostTimeCalculate calculate = (CostTimeCalculate) context.getBean("costTime");

		AsyncInvoker invoker = new AsyncInvoker();

		try {
			CostTime elapse0 = invoker.submit(new AsyncCallback<CostTime>() {
				@Override
				public CostTime call() {
					throw new RuntimeException("calculate fail 1!");
				}
			});

			System.out.println("1 async nettyrpc call:[" + "result:" + elapse0 + ", status:[" + ((AsyncCallObject) elapse0)._getStatus() + "]");
		} catch (AsyncCallException e) {
			System.out.println(e.getMessage());
			context.destroy();
			return;
		}

		context.destroy();
	}
}
