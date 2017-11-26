package com.newlandframework.test.jdbc;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import com.newlandframework.rpc.services.JdbcPersonManage;
import com.newlandframework.rpc.services.pojo.Person;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ContextConfiguration("classpath:rpc-invoke-config-jdbc-client.xml")
@RunWith(SpringRunner.class)
public class NettyRpcJdbcClientTest {

	@Resource(name = "personManageJdbc")
	private JdbcPersonManage manage;

	// 确保先启动NettyRPC服务端应用:NettyRpcJdbcServerTest，再运行NettyRpcJdbcClientTest、NettyRpcJdbcClientErrorTest！
	@Test
	public void testNettyRpcJdbcClient() {

		try {
			Person p = new Person();
			p.setId(1);
			p.setName("小好");
			p.setAge(2);
			p.setBirthday(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2015-08-11 16:28:00"));
			int result = manage.save(p);
			manage.query(p);
			log.info("call pojo rpc result:{}", result);
			log.info("---------------------------------------------");

			List<Person> list = manage.query();
			list.stream().map(Person::toString).forEach(log::info);
		} catch (ParseException e) {
			log.error(e.getMessage(), e);
		}
	}
}
