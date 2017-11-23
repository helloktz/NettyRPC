package com.newlandframework.rpc.spring;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

import com.google.common.eventbus.EventBus;
import com.newlandframework.rpc.event.ClientStopEvent;
import com.newlandframework.rpc.event.ClientStopEventListener;
import com.newlandframework.rpc.netty.MessageSendExecutor;
import com.newlandframework.rpc.serialize.RpcSerializeProtocol;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class NettyRpcReference implements FactoryBean<Object>, InitializingBean, DisposableBean {
	@Getter
	@Setter
	private String interfaceName;
	@Getter
	@Setter
	private String ipAddr;
	@Getter
	@Setter
	private String protocol;
	@Getter
	@Setter
	private EventBus eventBus = new EventBus();

	@Override
	public void destroy() throws Exception {
		eventBus.post(new ClientStopEvent(0));
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		MessageSendExecutor.getInstance().setRpcServerLoader(ipAddr, RpcSerializeProtocol.valueOf(protocol));
		ClientStopEventListener listener = new ClientStopEventListener();
		eventBus.register(listener);
	}

	@Override
	public Object getObject() throws Exception {
		return MessageSendExecutor.getInstance().execute(getObjectType());
	}

	@Override
	public Class<?> getObjectType() {
		try {
			return this.getClass().getClassLoader().loadClass(interfaceName);
		} catch (ClassNotFoundException e) {
			log.error("spring analyze fail!", e);
		}
		return null;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}
}
