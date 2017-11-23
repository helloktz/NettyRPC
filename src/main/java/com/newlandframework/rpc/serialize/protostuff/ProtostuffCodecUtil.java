package com.newlandframework.rpc.serialize.protostuff;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import com.newlandframework.rpc.serialize.MessageCodecUtil;

import io.netty.buffer.ByteBuf;
import lombok.Cleanup;

public class ProtostuffCodecUtil implements MessageCodecUtil {
	private ProtostuffSerializePool pool = ProtostuffSerializePool.getProtostuffPoolInstance();
	private boolean rpcDirect = false;

	public boolean isRpcDirect() {
		return rpcDirect;
	}

	public void setRpcDirect(boolean rpcDirect) {
		this.rpcDirect = rpcDirect;
	}

	@Override
	public void encode(final ByteBuf out, final Object message) throws IOException {
		@Cleanup
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		ProtostuffSerialize protostuffSerialization = pool.borrow();
		protostuffSerialization.serialize(byteArrayOutputStream, message);
		byte[] body = byteArrayOutputStream.toByteArray();
		int dataLength = body.length;
		out.writeInt(dataLength);
		out.writeBytes(body);
		pool.restore(protostuffSerialization);
	}

	@Override
	public Object decode(byte[] body) throws IOException {
		@Cleanup
		ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(body);
		ProtostuffSerialize protostuffSerialization = pool.borrow();
		protostuffSerialization.setRpcDirect(rpcDirect);
		Object obj = protostuffSerialization.deserialize(byteArrayInputStream);
		pool.restore(protostuffSerialization);
		return obj;
	}
}
