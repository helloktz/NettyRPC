package com.newlandframework.rpc.jmx;

import static com.newlandframework.rpc.core.RpcSystemConfig.DELIMITER;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.rmi.registry.LocateRegistry;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Semaphore;

import javax.management.JMException;
import javax.management.MBeanServer;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXConnectorServer;
import javax.management.remote.JMXConnectorServerFactory;
import javax.management.remote.JMXServiceURL;

import org.apache.commons.lang3.StringUtils;

import com.newlandframework.rpc.netty.MessageRecvExecutor;
import com.newlandframework.rpc.parallel.AbstractDaemonThread;
import com.newlandframework.rpc.parallel.SemaphoreWrapper;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ModuleMetricsHandler extends AbstractModuleMetricsHandler {
	public static final String MBEAN_NAME = "com.newlandframework.rpc:type=ModuleMetricsHandler";
	public static final int MODULE_METRICS_JMX_PORT = 1098;
	private String moduleMetricsJmxUrl = "";
	private Semaphore semaphore = new Semaphore(0);
	private SemaphoreWrapper semaphoreWrapper = new SemaphoreWrapper(semaphore);
	private static final ModuleMetricsHandler INSTANCE = new ModuleMetricsHandler();
	@Getter
	private MBeanServerConnection connection;
	@Getter
	@Setter
	private CountDownLatch latch = new CountDownLatch(1);
	private ModuleMetricsListener listener = new ModuleMetricsListener();

	public static ModuleMetricsHandler getInstance() {
		return INSTANCE;
	}

	private ModuleMetricsHandler() {
		super();
	}

	@Override
	protected ModuleMetricsVisitor visitCriticalSection(String moduleName, String methodName) {
		final String method = methodName.trim();
		final String module = moduleName.trim();

		// JMX度量临界区要注意线程间的并发竞争,否则会统计数据失真
		Optional<ModuleMetricsVisitor> option = visitorList.stream().filter(m -> module.equals(m.getModuleName()) && method.equals(m.getMethodName())).findFirst();

		return option.orElseGet(() -> {
			ModuleMetricsVisitor visitor = new ModuleMetricsVisitor(module, method);
			addModuleMetricsVisitor(visitor);
			return visitor;
		});
	}

	public void start() {
		new AbstractDaemonThread() {
			@Override
			public String getDeamonThreadName() {
				return ModuleMetricsHandler.class.getSimpleName();
			}

			@Override
			public void run() {
				MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
				try {
					latch.await();
					LocateRegistry.createRegistry(MODULE_METRICS_JMX_PORT);
					MessageRecvExecutor ref = MessageRecvExecutor.getInstance();
					String ipAddr = StringUtils.isNotEmpty(ref.getServerAddress()) ? StringUtils.substringBeforeLast(ref.getServerAddress(), DELIMITER) : "localhost";
					moduleMetricsJmxUrl = "service:jmx:rmi:///jndi/rmi://" + ipAddr + ":" + MODULE_METRICS_JMX_PORT + "/NettyRPCServer";
					JMXServiceURL url = new JMXServiceURL(moduleMetricsJmxUrl);
					JMXConnectorServer cs = JMXConnectorServerFactory.newJMXConnectorServer(url, null, mbs);

					ObjectName name = new ObjectName(MBEAN_NAME);

					mbs.registerMBean(ModuleMetricsHandler.this, name);
					mbs.addNotificationListener(name, listener, null, null);
					cs.start();

					semaphoreWrapper.release();

					log.info("NettyRPC JMX server is start success!");
					log.info("jmx-url:[ {} ]", moduleMetricsJmxUrl);
				} catch (JMException | IOException | InterruptedException e) {
					log.error(e.getMessage(), e);
				}
			}
		}.start();
	}

	public void stop() {
		MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
		try {
			ObjectName name = new ObjectName(MBEAN_NAME);
			mbs.unregisterMBean(name);
			ExecutorService executor = getExecutor();
			executor.shutdown();
			while (!executor.isTerminated()) {

			}
		} catch (JMException e) {
			log.error(e.getMessage(), e);
		}
	}

	public MBeanServerConnection connect() {
		try {
			if (!semaphoreWrapper.isRelease()) {
				semaphoreWrapper.acquire();
			}

			JMXServiceURL url = new JMXServiceURL(moduleMetricsJmxUrl);
			connection = JMXConnectorFactory.connect(url, null).getMBeanServerConnection();
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}
		return connection;
	}
}
