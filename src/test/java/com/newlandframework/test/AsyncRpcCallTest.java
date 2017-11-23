package com.newlandframework.test;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.newlandframework.rpc.async.AsyncCallObject;
import com.newlandframework.rpc.async.AsyncCallback;
import com.newlandframework.rpc.async.AsyncInvoker;
import com.newlandframework.rpc.services.CostTimeCalculate;
import com.newlandframework.rpc.services.pojo.CostTime;

public class AsyncRpcCallTest {
	public static void main(String[] args) {
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("classpath:rpc-invoke-config-client.xml");

		final CostTimeCalculate calculate = (CostTimeCalculate) context.getBean("costTime");

		long start = 0, end = 0;
		start = System.currentTimeMillis();

		AsyncInvoker invoker = new AsyncInvoker();

		CostTime elapse0 = invoker.submit(new AsyncCallback<CostTime>() {
			@Override
			public CostTime call() {
				return calculate.calculate();
			}
		});

		CostTime elapse1 = invoker.submit(new AsyncCallback<CostTime>() {
			@Override
			public CostTime call() {
				return calculate.calculate();
			}
		});

		CostTime elapse2 = invoker.submit(new AsyncCallback<CostTime>() {
			@Override
			public CostTime call() {
				return calculate.calculate();
			}
		});

		System.out.println("1 async nettyrpc call:[" + "result:" + elapse0 + ", status:[" + ((AsyncCallObject) elapse0)._getStatus() + "]");
		System.out.println("2 async nettyrpc call:[" + "result:" + elapse1 + ", status:[" + ((AsyncCallObject) elapse1)._getStatus() + "]");
		System.out.println("3 async nettyrpc call:[" + "result:" + elapse2 + ", status:[" + ((AsyncCallObject) elapse2)._getStatus() + "]");

		end = System.currentTimeMillis();

		System.out.println("nettyrpc async calculate time:" + (end - start));

		context.destroy();
	}
}
