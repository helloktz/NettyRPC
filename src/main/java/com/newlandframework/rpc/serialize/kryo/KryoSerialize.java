package com.newlandframework.rpc.serialize.kryo;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.pool.KryoPool;
import com.newlandframework.rpc.serialize.RpcSerialize;

import lombok.Cleanup;

public class KryoSerialize implements RpcSerialize {

	private KryoPool pool = null;

	public KryoSerialize(final KryoPool pool) {
		this.pool = pool;
	}

	@Override
	public void serialize(OutputStream output, Object object) throws IOException {
		Kryo kryo = pool.borrow();
		@Cleanup
		Output out = new Output(output);
		kryo.writeClassAndObject(out, object);
		pool.release(kryo);
	}

	@Override
	public Object deserialize(InputStream input) throws IOException {
		Kryo kryo = pool.borrow();
		@Cleanup
		Input in = new Input(input);
		Object result = kryo.readClassAndObject(in);
		pool.release(kryo);
		return result;
	}
}
