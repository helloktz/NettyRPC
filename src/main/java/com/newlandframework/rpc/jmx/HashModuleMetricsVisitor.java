package com.newlandframework.rpc.jmx;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.newlandframework.rpc.core.ReflectionUtils;
import com.newlandframework.rpc.core.RpcSystemConfig;
import com.newlandframework.rpc.netty.MessageRecvExecutor;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class HashModuleMetricsVisitor {
	@Setter
	@Getter
	private List<List<ModuleMetricsVisitor>> hashVisitorList = new ArrayList<List<ModuleMetricsVisitor>>();

	private static final HashModuleMetricsVisitor INSTANCE = new HashModuleMetricsVisitor();

	public static HashModuleMetricsVisitor getInstance() {
		return INSTANCE;
	}

	private HashModuleMetricsVisitor() {
		init();
	}

	public int getHashModuleMetricsVisitorListSize() {
		return hashVisitorList.size();
	}

	private void init() {
		Map<String, Object> map = MessageRecvExecutor.getInstance().getHandlerMap();
		ReflectionUtils utils = new ReflectionUtils();
		Set<String> s = map.keySet();
		Iterator<String> iter = s.iterator();
		String key;
		while (iter.hasNext()) {
			key = iter.next();
			try {
				List<String> list = utils.getClassMethodSignature(Class.forName(key));
				for (String signature : list) {
					List<ModuleMetricsVisitor> visitorList = new ArrayList<ModuleMetricsVisitor>();
					for (int i = 0; i < RpcSystemConfig.SYSTEM_PROPERTY_JMX_METRICS_HASH_NUMS; i++) {
						ModuleMetricsVisitor visitor = new ModuleMetricsVisitor(key, signature);
						visitor.setHashKey(i);
						visitorList.add(visitor);
					}
					hashVisitorList.add(visitorList);
				}
			} catch (ClassNotFoundException e) {
				log.error(e);
			}
		}
	}

	public void signal() {
		ModuleMetricsHandler.getInstance().getLatch().countDown();
	}
}
