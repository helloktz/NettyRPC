package com.newlandframework.rpc.netty;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import com.newlandframework.rpc.core.ReflectionUtils;
import com.newlandframework.rpc.event.AbstractInvokeEventBus.ModuleEvent;
import com.newlandframework.rpc.event.InvokeEventBusFacade;
import com.newlandframework.rpc.event.InvokeEventWatcher;
import com.newlandframework.rpc.event.InvokeFailObserver;
import com.newlandframework.rpc.event.InvokeFilterObserver;
import com.newlandframework.rpc.event.InvokeObserver;
import com.newlandframework.rpc.event.InvokeSuccObserver;
import com.newlandframework.rpc.filter.ServiceFilterBinder;
import com.newlandframework.rpc.jmx.ModuleMetricsHandler;
import com.newlandframework.rpc.jmx.ModuleMetricsVisitor;
import com.newlandframework.rpc.model.MessageRequest;
import com.newlandframework.rpc.model.MessageResponse;
import com.newlandframework.rpc.parallel.SemaphoreWrapperFactory;

public class MessageRecvInitializeTask extends AbstractMessageRecvInitializeTask {
	private AtomicReference<ModuleMetricsVisitor> visitor = new AtomicReference<>();
	private AtomicReference<InvokeEventBusFacade> facade = new AtomicReference<>();
	private AtomicReference<InvokeEventWatcher> watcher = new AtomicReference<>(new InvokeEventWatcher());
	private SemaphoreWrapperFactory factory = SemaphoreWrapperFactory.getInstance();

	public MessageRecvInitializeTask(MessageRequest request, MessageResponse response, Map<String, Object> handlerMap) {
		super(request, response, handlerMap);
	}

	@Override
	protected void injectInvoke() {
		Class cls = handlerMap.get(request.getClassName()).getClass();
		boolean binder = ServiceFilterBinder.class.isAssignableFrom(cls);
		if (binder) {
			cls = ((ServiceFilterBinder) handlerMap.get(request.getClassName())).getObject().getClass();
		}

		ReflectionUtils utils = new ReflectionUtils();

		try {
			Method method = ReflectionUtils.getDeclaredMethod(cls, request.getMethodName(), request.getTypeParameters());
			utils.listMethod(method, false);
			String signatureMethod = utils.getProvider().toString();
			visitor.set(ModuleMetricsHandler.getInstance().visit(request.getClassName(), signatureMethod));
			facade.set(new InvokeEventBusFacade(ModuleMetricsHandler.getInstance(), visitor.get().getModuleName(), visitor.get().getMethodName()));
			watcher.get().addObserver(new InvokeObserver(facade.get(), visitor.get()));
			watcher.get().watch(ModuleEvent.INVOKE_EVENT);
		} finally {
			utils.clearProvider();
		}
	}

	@Override
	protected void injectSuccInvoke(long invokeTimespan) {
		watcher.get().addObserver(new InvokeSuccObserver(facade.get(), visitor.get(), invokeTimespan));
		watcher.get().watch(ModuleEvent.INVOKE_SUCC_EVENT);
	}

	@Override
	protected void injectFailInvoke(Throwable error) {
		watcher.get().addObserver(new InvokeFailObserver(facade.get(), visitor.get(), error));
		watcher.get().watch(ModuleEvent.INVOKE_FAIL_EVENT);
	}

	@Override
	protected void injectFilterInvoke() {
		watcher.get().addObserver(new InvokeFilterObserver(facade.get(), visitor.get()));
		watcher.get().watch(ModuleEvent.INVOKE_FILTER_EVENT);
	}

	@Override
	protected void acquire() {
		factory.acquire();
	}

	@Override
	protected void release() {
		factory.release();
	}
}
