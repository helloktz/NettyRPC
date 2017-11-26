package com.newlandframework.rpc.spring;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;

import com.newlandframework.rpc.event.ServerStartEvent;
import com.newlandframework.rpc.filter.Filter;
import com.newlandframework.rpc.filter.ServiceFilterBinder;
import com.newlandframework.rpc.netty.MessageRecvExecutor;

import lombok.Getter;
import lombok.Setter;

public class NettyRpcService implements ApplicationContextAware, ApplicationListener<ApplicationEvent> {
	@Getter
	@Setter
	private String interfaceName;
	@Getter
	@Setter
	private String ref;
	@Getter
	@Setter
	private String filter;
	@Getter
	private ApplicationContext applicationContext;

	@Override
	public void onApplicationEvent(ApplicationEvent event) {
		ServiceFilterBinder binder = new ServiceFilterBinder();

		if (StringUtils.isBlank(filter) || !(applicationContext.getBean(filter) instanceof Filter)) {
			binder.setObject(applicationContext.getBean(ref));
		} else {
			binder.setObject(applicationContext.getBean(ref));
			binder.setFilter((Filter) applicationContext.getBean(filter));
		}

		MessageRecvExecutor.getInstance().getHandlerMap().put(interfaceName, binder);
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
		applicationContext.publishEvent(new ServerStartEvent(new Object()));
	}
}
