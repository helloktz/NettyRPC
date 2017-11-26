package com.newlandframework.test;

import java.util.concurrent.CountDownLatch;

import com.newlandframework.rpc.exception.InvokeTimeoutException;
import com.newlandframework.rpc.services.MultiCalculate;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MultiCalcParallelRequestThread implements Runnable {

	private CountDownLatch signal;
	private CountDownLatch finish;
	private int taskNumber = 0;
	private MultiCalculate calc;

	public MultiCalcParallelRequestThread(MultiCalculate calc, CountDownLatch signal, CountDownLatch finish, int taskNumber) {
		this.signal = signal;
		this.finish = finish;
		this.taskNumber = taskNumber;
		this.calc = calc;
	}

	@Override
	public void run() {
		try {
			signal.await();
			int multi = calc.multi(taskNumber, taskNumber);
			System.out.println("calc multi result:[" + multi + "]");
		} catch (InterruptedException ie) {
			log.error(ie.getMessage(), ie);
			Thread.currentThread().interrupt();
		} catch (InvokeTimeoutException ite) {
			log.error(ite.getMessage(), ite);
		} finally {
			finish.countDown();
		}
	}
}
