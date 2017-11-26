package com.newlandframework.rpc.serialize.hessian;

import static com.newlandframework.rpc.core.RpcSystemConfig.SERIALIZE_POOL_MAX_TOTAL;
import static com.newlandframework.rpc.core.RpcSystemConfig.SERIALIZE_POOL_MAX_WAIT_MILLIS;
import static com.newlandframework.rpc.core.RpcSystemConfig.SERIALIZE_POOL_MIN_EVICTABLE_IDLE_TIME_MILLIS;
import static com.newlandframework.rpc.core.RpcSystemConfig.SERIALIZE_POOL_MIN_IDLE;

import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class HessianSerializePool {
	@Getter
	private GenericObjectPool<HessianSerialize> hessianPool;
	private static volatile HessianSerializePool poolFactory = null;

	public static HessianSerializePool getHessianPoolInstance() {
		if (poolFactory == null) {
			synchronized (HessianSerializePool.class) {
				if (poolFactory == null) {
					poolFactory = new HessianSerializePool(SERIALIZE_POOL_MAX_TOTAL, SERIALIZE_POOL_MIN_IDLE, SERIALIZE_POOL_MAX_WAIT_MILLIS, SERIALIZE_POOL_MIN_EVICTABLE_IDLE_TIME_MILLIS);
				}
			}
		}
		return poolFactory;
	}

	public HessianSerializePool(final int maxTotal, final int minIdle, final long maxWaitMillis, final long minEvictableIdleTimeMillis) {
		hessianPool = new GenericObjectPool<>(new HessianSerializeFactory());

		GenericObjectPoolConfig config = new GenericObjectPoolConfig();

		config.setMaxTotal(maxTotal);
		config.setMinIdle(minIdle);
		config.setMaxWaitMillis(maxWaitMillis);
		config.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);

		hessianPool.setConfig(config);
	}

	public HessianSerialize borrow() {
		try {
			return getHessianPool().borrowObject();
		} catch (Exception ex) {
			log.error(ex.getMessage(), ex);
			return null;
		}
	}

	public void restore(HessianSerialize object) {
		getHessianPool().returnObject(object);
	}
}
