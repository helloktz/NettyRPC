package com.newlandframework.rpc.serialize.hessian;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import com.newlandframework.rpc.serialize.MessageCodecUtil;

import io.netty.buffer.ByteBuf;
import lombok.Cleanup;

public class HessianCodecUtil implements MessageCodecUtil {

	private HessianSerializePool pool = HessianSerializePool.getHessianPoolInstance();

	@Override
	public void encode(final ByteBuf out, final Object message) throws IOException {
		@Cleanup
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		HessianSerialize hessianSerialization = pool.borrow();
		hessianSerialization.serialize(byteArrayOutputStream, message);
		byte[] body = byteArrayOutputStream.toByteArray();
		int dataLength = body.length;
		out.writeInt(dataLength);
		out.writeBytes(body);
		pool.restore(hessianSerialization);
	}

	@Override
	public Object decode(byte[] body) throws IOException {
		@Cleanup
		ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(body);
		HessianSerialize hessianSerialization = pool.borrow();
		Object object = hessianSerialization.deserialize(byteArrayInputStream);
		pool.restore(hessianSerialization);
		return object;
	}
}
