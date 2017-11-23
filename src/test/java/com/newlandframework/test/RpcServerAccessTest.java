package com.newlandframework.test;

import java.io.InputStreamReader;
import java.io.Reader;

import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.DefaultResourceLoader;

import com.google.common.io.CharStreams;
import com.newlandframework.rpc.compiler.AccessAdaptive;

import lombok.Cleanup;
import lombok.SneakyThrows;

public class RpcServerAccessTest {

	@SneakyThrows
	public static void main(String[] args) {
		DefaultResourceLoader resource = new DefaultResourceLoader();
		@Cleanup
		Reader input = new InputStreamReader(resource.getResource("AccessProvider.tpl").getInputStream(), "UTF-8");
		String javaSource = CharStreams.toString(input);

		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("classpath:rpc-invoke-config-client.xml");

		AccessAdaptive provider = (AccessAdaptive) context.getBean("access");

		String result = (String) provider.invoke(javaSource, "getRpcServerTime", new Object[] { new String("XiaoHaoBaby") });
		System.out.println(result);

		provider.invoke(javaSource, "sayHello", new Object[0]);

		context.destroy();
	}
}
