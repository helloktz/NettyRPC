package com.newlandframework.test;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import com.newlandframework.rpc.exception.InvokeModuleException;
import com.newlandframework.rpc.services.PersonManage;
import com.newlandframework.rpc.services.pojo.Person;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RunWith(SpringRunner.class)
@ContextConfiguration("classpath:rpc-invoke-config-client.xml")
public class PojoCallErrorTest {

	@Resource(name = "personManage")
	private PersonManage manage;

	@Test
	public void testPojoCallError() {
		test1(manage);
		test2(manage);
	}

	public void test1(PersonManage manage) {
		try {
			manage.check();
		} catch (InvokeModuleException e) {
			log.error(e.getMessage(), e);
		}
	}

	public void test2(PersonManage manage) {
		try {
			Person p = new Person();
			p.setId(20150811);
			p.setName("XiaoHaoBaby");
			p.setAge(1);
			manage.checkAge(p);
		} catch (InvokeModuleException e) {
			log.error(e.getMessage(), e);
		}
	}
}
