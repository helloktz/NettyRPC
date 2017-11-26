package com.newlandframework.rpc.serialize;

import java.io.IOException;

import io.netty.buffer.ByteBuf;

public interface MessageCodecUtil {

	void encode(final ByteBuf out, final Object message) throws IOException;

	Object decode(byte[] body) throws IOException;
}
