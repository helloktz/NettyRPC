package com.newlandframework.rpc.services.impl;

import java.util.concurrent.TimeUnit;

import com.newlandframework.rpc.services.CostTimeCalculate;
import com.newlandframework.rpc.services.pojo.CostTime;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CostTimeCalculateImpl implements CostTimeCalculate {
	@Override
	public CostTime calculate() {
		CostTime elapse = new CostTime();
		try {
			long start = 0;
			long end = 0;
			start = System.currentTimeMillis();
			// 模拟耗时操作
			TimeUnit.SECONDS.sleep(3);
			end = System.currentTimeMillis();

			long interval = end - start;
			elapse.setElapse(interval);
			elapse.setDetail("I'm XiaoHaoBaby,cost time operate succ!");
			log.info("calculate time:{}", interval);
			return elapse;
		} catch (InterruptedException e) {
			log.error(e.getMessage(), e);
			Thread.currentThread().interrupt();
			elapse.setDetail("I'm XiaoHaoBaby,cost time operate fail!");
			return elapse;
		}
	}

	@Override
	public CostTime busy() {
		CostTime elapse = new CostTime();
		try {
			long start = 0;
			long end = 0;
			start = System.currentTimeMillis();
			// 模拟耗时操作,超过nettyrpc.default.msg.timeout定义的上限
			TimeUnit.SECONDS.sleep(35);
			end = System.currentTimeMillis();

			long interval = end - start;
			elapse.setElapse(interval);
			elapse.setDetail("I'm XiaoHao,I'm busy now!");
			log.info("calculate time:{}", interval);
			return elapse;
		} catch (InterruptedException e) {
			log.error(e.getMessage(), e);
			Thread.currentThread().interrupt();
			elapse.setDetail("I'm XiaoHao,I'm handle error now!");
			return elapse;
		}
	}
}
