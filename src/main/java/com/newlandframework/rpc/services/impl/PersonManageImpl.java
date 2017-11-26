package com.newlandframework.rpc.services.impl;

import java.util.concurrent.TimeUnit;

import com.newlandframework.rpc.exception.RPCRuntimeException;
import com.newlandframework.rpc.services.PersonManage;
import com.newlandframework.rpc.services.pojo.Person;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PersonManageImpl implements PersonManage {
	@Override
	public int save(Person p) {
		// your business logic code here!
		log.info("person data[{}] has save!", p);
		return 0;
	}

	@Override
	public void query(Person p) {
		// your business logic code here!
		try {
			TimeUnit.SECONDS.sleep(3);
			log.info("person data[{}] has query!", p);
		} catch (InterruptedException e) {
			log.error(e.getMessage(), e);
			Thread.currentThread().interrupt();
		}
	}

	@Override
	public void query(long timeout) {
		// your business logic code here!
		try {
			TimeUnit.SECONDS.sleep(timeout);
		} catch (InterruptedException e) {
			log.error(e.getMessage(), e);
			Thread.currentThread().interrupt();
		}
	}

	@Override
	public void check() {
		throw new RPCRuntimeException("person check fail!");
	}

	@Override
	public boolean checkAge(Person p) {
		if (p.getAge() < 18) {
			throw new RPCRuntimeException("person check age fail!");
		} else {
			log.info("person check age succ!");
			return true;
		}
	}
}
