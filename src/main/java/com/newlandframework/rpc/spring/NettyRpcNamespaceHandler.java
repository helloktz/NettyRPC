package com.newlandframework.rpc.spring;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import com.google.common.io.CharStreams;

import lombok.Cleanup;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NettyRpcNamespaceHandler extends NamespaceHandlerSupport {
	static {
		Resource resource = new ClassPathResource("NettyRPC-logo.txt");
		if (resource.exists()) {
			try {
				@Cleanup
				Reader reader = new InputStreamReader(resource.getInputStream(), "UTF-8");
				String text = CharStreams.toString(reader);
				System.out.println(text);
			} catch (IOException e) {
				log.info(e.getMessage(), e);
			}
		} else {
			System.out.println("");
			System.out.println(" _      _____ _____ _____ ___  _ ____  ____  ____ ");
			System.out.println("/ \\  /|/  __//__ __Y__ __\\\\  \\///  __\\/  __\\/   _\\");
			System.out.println("| |\\ |||  \\    / \\   / \\   \\  / |  \\/||  \\/||  /  ");
			System.out.println("| | \\|||  /_   | |   | |   / /  |    /|  __/|  \\_ ");
			System.out.println("\\_/  \\|\\____\\  \\_/   \\_/  /_/   \\_/\\_\\\\_/   \\____/");
			System.out.println("[NettyRPC 2.0]");
			System.out.println("");
		}
	}

	@Override
	public void init() {
		registerBeanDefinitionParser("service", new NettyRpcServiceParser());
		registerBeanDefinitionParser("registry", new NettyRpcRegisteryParser());
		registerBeanDefinitionParser("reference", new NettyRpcReferenceParser());
	}
}
