package com.newlandframework.rpc.serialize.protostuff;

import static com.newlandframework.rpc.core.RpcSystemConfig.SERIALIZE_POOL_MAX_TOTAL;
import static com.newlandframework.rpc.core.RpcSystemConfig.SERIALIZE_POOL_MAX_WAIT_MILLIS;
import static com.newlandframework.rpc.core.RpcSystemConfig.SERIALIZE_POOL_MIN_EVICTABLE_IDLE_TIME_MILLIS;
import static com.newlandframework.rpc.core.RpcSystemConfig.SERIALIZE_POOL_MIN_IDLE;

import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ProtostuffSerializePool {
	@Getter
	private GenericObjectPool<ProtostuffSerialize> protostuffPool;
	private static volatile ProtostuffSerializePool poolFactory = null;

	public static ProtostuffSerializePool getProtostuffPoolInstance() {
		if (poolFactory == null) {
			synchronized (ProtostuffSerializePool.class) {
				if (poolFactory == null) {
					poolFactory = new ProtostuffSerializePool(SERIALIZE_POOL_MAX_TOTAL, SERIALIZE_POOL_MIN_IDLE, SERIALIZE_POOL_MAX_WAIT_MILLIS, SERIALIZE_POOL_MIN_EVICTABLE_IDLE_TIME_MILLIS);
				}
			}
		}
		return poolFactory;
	}

	public ProtostuffSerializePool(final int maxTotal, final int minIdle, final long maxWaitMillis, final long minEvictableIdleTimeMillis) {
		protostuffPool = new GenericObjectPool<ProtostuffSerialize>(new ProtostuffSerializeFactory());

		GenericObjectPoolConfig config = new GenericObjectPoolConfig();

		config.setMaxTotal(maxTotal);
		config.setMinIdle(minIdle);
		config.setMaxWaitMillis(maxWaitMillis);
		config.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);

		protostuffPool.setConfig(config);
	}

	public ProtostuffSerialize borrow() {
		try {
			return getProtostuffPool().borrowObject();
		} catch (Exception ex) {
			log.error(ex);
			return null;
		}
	}

	public void restore(final ProtostuffSerialize object) {
		getProtostuffPool().returnObject(object);
	}
}
