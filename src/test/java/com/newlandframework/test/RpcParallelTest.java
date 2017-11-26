package com.newlandframework.test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.apache.commons.lang3.time.StopWatch;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import com.newlandframework.rpc.services.AddCalculate;
import com.newlandframework.rpc.services.MultiCalculate;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RunWith(SpringRunner.class)
@ContextConfiguration("classpath:rpc-invoke-config-client.xml")
public class RpcParallelTest {

	@Resource(name = "addCalc")
	private AddCalculate addCalculate;
	@Resource(name = "multiCalc")
	private MultiCalculate multiCalculate;

	@Test
	@SneakyThrows
	public void testRpcParallel() {
		// 并行度1000
		int parallel = 1000;

		for (int i = 0; i < 1; i++) {
			addTask(addCalculate, parallel);
			multiTask(multiCalculate, parallel);
			log.info("Netty RPC Server 消息协议序列化第[{}]轮并发验证结束!", i);
		}
	}

	public void parallelAddCalcTask(AddCalculate calc, int parallel) throws InterruptedException {
		// 开始计时
		StopWatch sw = new StopWatch();
		sw.start();

		CountDownLatch signal = new CountDownLatch(1);
		CountDownLatch finish = new CountDownLatch(parallel);

		for (int index = 0; index < parallel; index++) {
			AddCalcParallelRequestThread client = new AddCalcParallelRequestThread(calc, signal, finish, index);
			new Thread(client).start();
		}

		signal.countDown();
		finish.await();
		sw.stop();
		log.info("加法计算RPC调用总共耗时: [{}] 毫秒", sw.getTime());
	}

	public void parallelMultiCalcTask(MultiCalculate calc, int parallel) throws InterruptedException {
		// 开始计时
		StopWatch sw = new StopWatch();
		sw.start();

		CountDownLatch signal = new CountDownLatch(1);
		CountDownLatch finish = new CountDownLatch(parallel);

		for (int index = 0; index < parallel; index++) {
			MultiCalcParallelRequestThread client = new MultiCalcParallelRequestThread(calc, signal, finish, index);
			new Thread(client).start();
		}

		signal.countDown();
		finish.await();
		sw.stop();
		log.info("乘法计算RPC调用总共耗时: [{}] 毫秒", sw.getTime());
	}

	public void addTask(AddCalculate calc, int parallel) throws InterruptedException {
		parallelAddCalcTask(calc, parallel);
		TimeUnit.MILLISECONDS.sleep(30);
	}

	public void multiTask(MultiCalculate calc, int parallel) throws InterruptedException {
		parallelMultiCalcTask(calc, parallel);
		TimeUnit.MILLISECONDS.sleep(30);
	}
}
