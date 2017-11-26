package com.newlandframework.rpc.serialize.kryo;

import org.objenesis.strategy.StdInstantiatorStrategy;
import org.springframework.beans.factory.FactoryBean;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.pool.KryoPool;
import com.newlandframework.rpc.model.MessageRequest;
import com.newlandframework.rpc.model.MessageResponse;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class KryoPoolFactory implements FactoryBean<KryoPool> {

	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	private static final class KryoPoolHolder {
		private static final KryoPool INSTANCE = new KryoPool.Builder(() -> {
			Kryo kryo = new Kryo();
			kryo.setReferences(false);
			kryo.register(MessageRequest.class);
			kryo.register(MessageResponse.class);
			kryo.setInstantiatorStrategy(new StdInstantiatorStrategy());
			return kryo;
		}).build();
	}

	public static KryoPool getKryoPoolInstance() {
		return KryoPoolHolder.INSTANCE;
	}

	@Override
	public KryoPool getObject() throws Exception {
		return getKryoPoolInstance();
	}

	@Override
	public Class<?> getObjectType() {
		return KryoPool.class;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}
}
