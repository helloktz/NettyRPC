package com.newlandframework.rpc.jmx;

import static com.newlandframework.rpc.core.RpcSystemConfig.DELIMITER;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.rmi.registry.LocateRegistry;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Semaphore;

import javax.management.JMException;
import javax.management.MBeanServer;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXConnectorServer;
import javax.management.remote.JMXConnectorServerFactory;
import javax.management.remote.JMXServiceURL;

import org.apache.commons.collections4.Predicate;
import org.apache.commons.collections4.iterators.FilterIterator;
import org.apache.commons.lang3.StringUtils;

import com.newlandframework.rpc.netty.MessageRecvExecutor;
import com.newlandframework.rpc.parallel.AbstractDaemonThread;
import com.newlandframework.rpc.parallel.SemaphoreWrapper;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class ModuleMetricsHandler extends AbstractModuleMetricsHandler {
	public final static String MBEAN_NAME = "com.newlandframework.rpc:type=ModuleMetricsHandler";
	public final static int MODULE_METRICS_JMX_PORT = 1098;
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
	public List<ModuleMetricsVisitor> getModuleMetricsVisitor() {
		return super.getModuleMetricsVisitor();
	}

	@Override
	protected ModuleMetricsVisitor visitCriticalSection(String moduleName, String methodName) {
		final String method = methodName.trim();
		final String module = moduleName.trim();

		// FIXME: 2017/10/13 by tangjie
		// JMX度量临界区要注意线程间的并发竞争,否则会统计数据失真
		Iterator iterator = new FilterIterator(visitorList.iterator(), new Predicate() {
			@Override
			public boolean evaluate(Object object) {
				String statModuleName = ((ModuleMetricsVisitor) object).getModuleName();
				String statMethodName = ((ModuleMetricsVisitor) object).getMethodName();
				return statModuleName.compareTo(module) == 0 && statMethodName.compareTo(method) == 0;
			}
		});

		ModuleMetricsVisitor visitor = null;
		while (iterator.hasNext()) {
			visitor = (ModuleMetricsVisitor) iterator.next();
			break;
		}

		if (visitor != null) {
			return visitor;
		} else {
			visitor = new ModuleMetricsVisitor(module, method);
			addModuleMetricsVisitor(visitor);
			return visitor;
		}
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

					System.out.printf("NettyRPC JMX server is start success!\njmx-url:[ %s ]\n\n", moduleMetricsJmxUrl);
				} catch (JMException | IOException | InterruptedException e) {
					log.error(e);
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
			log.error(e);
		}
	}

	public MBeanServerConnection connect() {
		try {
			if (!semaphoreWrapper.isRelease()) {
				semaphoreWrapper.acquire();
			}

			JMXServiceURL url = new JMXServiceURL(moduleMetricsJmxUrl);
			JMXConnector jmxc = JMXConnectorFactory.connect(url, null);
			connection = jmxc.getMBeanServerConnection();
		} catch (IOException e) {
			log.error(e);
		}
		return connection;
	}
}
