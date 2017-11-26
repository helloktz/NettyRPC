package com.newlandframework.rpc.netty;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.concurrent.Callable;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.support.NameMatchMethodPointcutAdvisor;

import com.newlandframework.rpc.core.RpcSystemConfig;
import com.newlandframework.rpc.model.MessageRequest;
import com.newlandframework.rpc.model.MessageResponse;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractMessageRecvInitializeTask implements Callable<Boolean> {
	@Getter
	@Setter
	protected MessageRequest request = null;
	@Getter
	protected MessageResponse response = null;
	protected Map<String, Object> handlerMap = null;
	protected static final String METHOD_MAPPED_NAME = "invoke";
	@Getter
	@Setter
	protected boolean returnNotNull = true;
	protected long invokeTimespan;

	public AbstractMessageRecvInitializeTask(MessageRequest request, MessageResponse response, Map<String, Object> handlerMap) {
		this.request = request;
		this.response = response;
		this.handlerMap = handlerMap;
	}

	@Override
	public Boolean call() {
		try {
			acquire();
			response.setMessageId(request.getMessageId());
			injectInvoke();
			Object result = reflect(request);
			boolean isInvokeSucc = ((returnNotNull && result != null) || !returnNotNull);
			if (isInvokeSucc) {
				response.setResult(result);
				response.setError("");
				response.setReturnNotNull(returnNotNull);
				injectSuccInvoke(invokeTimespan);
			} else {
				log.error(RpcSystemConfig.FILTER_RESPONSE_MSG);
				response.setResult(null);
				response.setError(RpcSystemConfig.FILTER_RESPONSE_MSG);
				injectFilterInvoke();
			}
			return Boolean.TRUE;
		} catch (Exception t) {
			response.setError(ExceptionUtils.getStackTrace(t));
			log.error("RPC Server invoke error!", t);
			injectFailInvoke(t);
			return Boolean.FALSE;
		} finally {
			release();
		}
	}

	private Object reflect(MessageRequest request) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
		ProxyFactory weaver = new ProxyFactory(new MethodInvoker());
		NameMatchMethodPointcutAdvisor advisor = new NameMatchMethodPointcutAdvisor();
		advisor.setMappedName(METHOD_MAPPED_NAME);
		advisor.setAdvice(new MethodProxyAdvisor(handlerMap));
		weaver.addAdvisor(advisor);
		MethodInvoker mi = (MethodInvoker) weaver.getProxy();
		Object obj = mi.invoke(request);
		invokeTimespan = mi.getInvokeTimespan();
		setReturnNotNull(((MethodProxyAdvisor) advisor.getAdvice()).isReturnNotNull());
		return obj;
	}

	protected abstract void injectInvoke();

	protected abstract void injectSuccInvoke(long invokeTimespan);

	protected abstract void injectFailInvoke(Throwable error);

	protected abstract void injectFilterInvoke();

	protected abstract void acquire();

	protected abstract void release();
}
