package com.newlandframework.rpc.serialize.kryo;

import org.objenesis.strategy.StdInstantiatorStrategy;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.pool.KryoFactory;
import com.esotericsoftware.kryo.pool.KryoPool;
import com.newlandframework.rpc.model.MessageRequest;
import com.newlandframework.rpc.model.MessageResponse;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class KryoPoolFactory {

	private static volatile KryoPoolFactory poolFactory = null;

	private KryoFactory factory = new KryoFactory() {
		@Override
		public Kryo create() {
			Kryo kryo = new Kryo();
			kryo.setReferences(false);
			kryo.register(MessageRequest.class);
			kryo.register(MessageResponse.class);
			kryo.setInstantiatorStrategy(new StdInstantiatorStrategy());
			return kryo;
		}
	};

	@Getter
	private KryoPool pool = new KryoPool.Builder(factory).build();

	public static KryoPool getKryoPoolInstance() {
		if (poolFactory == null) {
			synchronized (KryoPoolFactory.class) {
				if (poolFactory == null) {
					poolFactory = new KryoPoolFactory();
				}
			}
		}
		return poolFactory.getPool();
	}
}
