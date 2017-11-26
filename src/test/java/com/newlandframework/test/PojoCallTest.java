package com.newlandframework.test;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import com.newlandframework.rpc.services.PersonManage;
import com.newlandframework.rpc.services.pojo.Person;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RunWith(SpringRunner.class)
@ContextConfiguration("classpath:rpc-invoke-config-client.xml")
public class PojoCallTest {

	@Resource(name = "personManage")
	private PersonManage manage;

	@Test
	public void testPojoCall() {

		Person p = new Person();
		p.setId(20150811);
		p.setName("XiaoHaoBaby");
		p.setAge(1);

		int result = manage.save(p);

		manage.query(p);

		log.info("call pojo rpc result:", result);
	}
}
