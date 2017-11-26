package com.newlandframework.test;

import java.io.InputStreamReader;
import java.io.Reader;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import com.google.common.io.CharStreams;
import com.newlandframework.rpc.compiler.AccessAdaptive;

import lombok.Cleanup;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RunWith(SpringRunner.class)
@ContextConfiguration("classpath:rpc-invoke-config-client.xml")
public class RpcServerAccessTest {

	@Resource(name = "access")
	private AccessAdaptive provider;

	@Test
	@SneakyThrows
	public void testRpcServerAccess() {
		@Cleanup
		Reader input = new InputStreamReader(new DefaultResourceLoader().getResource("AccessProvider.tpl").getInputStream(), "UTF-8");
		String javaSource = CharStreams.toString(input);

		String result = (String) provider.invoke(javaSource, "getRpcServerTime", new Object[] { new String("XiaoHaoBaby") });
		log.info(result);

		provider.invoke(javaSource, "sayHello", new Object[0]);
	}
}
