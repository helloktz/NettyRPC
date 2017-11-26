package com.newlandframework.test;

import java.util.concurrent.CountDownLatch;

import com.newlandframework.rpc.exception.InvokeTimeoutException;
import com.newlandframework.rpc.services.AddCalculate;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AddCalcParallelRequestThread implements Runnable {

	private CountDownLatch signal;
	private CountDownLatch finish;
	private int taskNumber = 0;
	private AddCalculate calc;

	public AddCalcParallelRequestThread(AddCalculate calc, CountDownLatch signal, CountDownLatch finish, int taskNumber) {
		this.signal = signal;
		this.finish = finish;
		this.taskNumber = taskNumber;
		this.calc = calc;
	}

	@Override
	public void run() {
		try {
			signal.await();
			int add = calc.add(taskNumber, taskNumber);
			log.info("calc add result:[{}]", add);
		} catch (InterruptedException | InvokeTimeoutException ex) {
			log.error(ex.getMessage(), ex);
		} finally {
			finish.countDown();
		}
	}
}
