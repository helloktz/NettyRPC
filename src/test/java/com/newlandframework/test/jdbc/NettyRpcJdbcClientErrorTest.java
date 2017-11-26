package com.newlandframework.test.jdbc;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import com.newlandframework.rpc.exception.InvokeModuleException;
import com.newlandframework.rpc.services.JdbcPersonManage;
import com.newlandframework.rpc.services.pojo.Person;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RunWith(SpringRunner.class)
@ContextConfiguration("classpath:rpc-invoke-config-jdbc-client.xml")
public class NettyRpcJdbcClientErrorTest {

	@Resource(name = "personManageJdbc")
	private JdbcPersonManage manage;

	// 确保先启动NettyRPC服务端应用:NettyRpcJdbcServerTest，再运行NettyRpcJdbcClientTest、NettyRpcJdbcClientErrorTest！
	@Test
	public void testNettyRpcJdbcClientError() {
		// 验证RPC调用服务端执行失败的情况！
		Person p = new Person();
		p.setId(20150811);
		p.setName("XiaoHaoBaby");
		p.setAge(999999999);

		try {
			int result = manage.save(p);
			log.info("call pojo rpc result:{}", result);
		} catch (InvokeModuleException e) {
			log.error(e.getMessage(), e);
		}
	}
}
