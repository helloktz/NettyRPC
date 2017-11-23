package com.newlandframework.test.jdbc;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.newlandframework.rpc.exception.InvokeModuleException;
import com.newlandframework.rpc.services.JdbcPersonManage;
import com.newlandframework.rpc.services.pojo.Person;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class NettyRpcJdbcClientErrorTest {
	// FIXME: 2017/9/25
	// 确保先启动NettyRPC服务端应用:NettyRpcJdbcServerTest，再运行NettyRpcJdbcClientTest、NettyRpcJdbcClientErrorTest！
	public static void main(String[] args) {
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("classpath:rpc-invoke-config-jdbc-client.xml");

		JdbcPersonManage manage = (JdbcPersonManage) context.getBean("personManageJdbc");

		// 验证RPC调用服务端执行失败的情况！
		Person p = new Person();
		p.setId(20150811);
		p.setName("XiaoHaoBaby");
		p.setAge(999999999);

		try {
			int result = manage.save(p);
			System.out.println("call pojo rpc result:" + result);
		} catch (InvokeModuleException e) {
			log.error(e);
			System.out.println(e.getMessage());
		} finally {
			context.destroy();
		}
	}
}
