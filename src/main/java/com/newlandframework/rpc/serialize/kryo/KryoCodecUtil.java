package com.newlandframework.rpc.serialize.kryo;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import com.esotericsoftware.kryo.pool.KryoPool;
import com.newlandframework.rpc.serialize.MessageCodecUtil;

import io.netty.buffer.ByteBuf;
import lombok.Cleanup;

public class KryoCodecUtil implements MessageCodecUtil {

	private KryoPool pool;

	public KryoCodecUtil(KryoPool pool) {
		this.pool = pool;
	}

	@Override
	public void encode(final ByteBuf out, final Object message) throws IOException {
		@Cleanup
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		KryoSerialize kryoSerialization = new KryoSerialize(pool);
		kryoSerialization.serialize(byteArrayOutputStream, message);
		byte[] body = byteArrayOutputStream.toByteArray();
		int dataLength = body.length;
		out.writeInt(dataLength);
		out.writeBytes(body);
	}

	@Override
	public Object decode(byte[] body) throws IOException {
		@Cleanup
		ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(body);
		KryoSerialize kryoSerialization = new KryoSerialize(pool);
		Object obj = kryoSerialization.deserialize(byteArrayInputStream);
		return obj;
	}
}
