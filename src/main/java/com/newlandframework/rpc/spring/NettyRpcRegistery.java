package com.newlandframework.rpc.spring;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.newlandframework.rpc.core.RpcSystemConfig;
import com.newlandframework.rpc.jmx.HashModuleMetricsVisitor;
import com.newlandframework.rpc.jmx.ModuleMetricsHandler;
import com.newlandframework.rpc.jmx.ThreadPoolMonitorProvider;
import com.newlandframework.rpc.netty.MessageRecvExecutor;
import com.newlandframework.rpc.serialize.RpcSerializeProtocol;

import lombok.Getter;
import lombok.Setter;

public class NettyRpcRegistery implements InitializingBean, DisposableBean {
	@Getter
	@Setter
	private String ipAddr;
	@Getter
	@Setter
	private String protocol;
	@Getter
	@Setter
	private String echoApiPort;
	private AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();

	@Override
	public void destroy() throws Exception {
		MessageRecvExecutor.getInstance().stop();

		if (RpcSystemConfig.SYSTEM_PROPERTY_JMX_METRICS_SUPPORT) {
			ModuleMetricsHandler handler = ModuleMetricsHandler.getInstance();
			handler.stop();
		}
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		MessageRecvExecutor ref = MessageRecvExecutor.getInstance();
		ref.setServerAddress(ipAddr);
		ref.setEchoApiPort(Integer.parseInt(echoApiPort));
		ref.setSerializeProtocol(Enum.valueOf(RpcSerializeProtocol.class, protocol));

		if (RpcSystemConfig.isMonitorServerSupport()) {
			context.register(ThreadPoolMonitorProvider.class);
			context.refresh();
		}

		ref.start();

		if (RpcSystemConfig.SYSTEM_PROPERTY_JMX_METRICS_SUPPORT) {
			HashModuleMetricsVisitor visitor = HashModuleMetricsVisitor.getInstance();
			visitor.signal();
			ModuleMetricsHandler handler = ModuleMetricsHandler.getInstance();
			handler.start();
		}
	}

}
