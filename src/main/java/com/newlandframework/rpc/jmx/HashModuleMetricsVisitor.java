package com.newlandframework.rpc.jmx;

import static com.newlandframework.rpc.core.RpcSystemConfig.SYSTEM_PROPERTY_JMX_METRICS_HASH_NUMS;
import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import com.newlandframework.rpc.core.ReflectionUtils;
import com.newlandframework.rpc.netty.MessageRecvExecutor;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HashModuleMetricsVisitor {
	@Setter
	@Getter
	private List<List<ModuleMetricsVisitor>> hashVisitorList = new ArrayList<>();

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
		final Map<String, ?> map = MessageRecvExecutor.getInstance().getHandlerMap();
		final ReflectionUtils utils = new ReflectionUtils();
		map.keySet().stream().forEach(key -> enrichHashVistors(utils, key));
	}

	private void enrichHashVistors(ReflectionUtils utils, String key) {
		try {
			List<String> list = utils.getClassMethodSignature(Class.forName(key));
			list.stream().forEach(signature -> hashVisitorList.add(generateVistors(key, signature)));
		} catch (ClassNotFoundException e) {
			log.error(e.getMessage(), e);
		}
	}

	private List<ModuleMetricsVisitor> generateVistors(String key, String signature) {
		List<ModuleMetricsVisitor> visitorList = IntStream.range(0, SYSTEM_PROPERTY_JMX_METRICS_HASH_NUMS).mapToObj(i -> {
			ModuleMetricsVisitor visitor = new ModuleMetricsVisitor(key, signature);
			visitor.setHashKey(i);
			return visitor;
		}).collect(toList());
		return visitorList;
	}

	public void signal() {
		ModuleMetricsHandler.getInstance().getLatch().countDown();
	}
}
