package com.newlandframework.rpc.serialize.protostuff;

import java.io.InputStream;
import java.io.OutputStream;

import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;

import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.Schema;
import com.newlandframework.rpc.model.MessageRequest;
import com.newlandframework.rpc.model.MessageResponse;
import com.newlandframework.rpc.serialize.RpcSerialize;

import lombok.Cleanup;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;

public class ProtostuffSerialize implements RpcSerialize {
	private static SchemaCache cachedSchema = SchemaCache.getInstance();
	private static Objenesis objenesis = new ObjenesisStd(true);
	@Getter
	@Setter
	private boolean rpcDirect = false;

	private static <T> Schema<T> getSchema(Class<T> cls) {
		return (Schema<T>) cachedSchema.get(cls);
	}

	@Override
	@SneakyThrows
	public Object deserialize(InputStream input) {
		Class cls = isRpcDirect() ? MessageRequest.class : MessageResponse.class;
		Object message = objenesis.newInstance(cls);
		Schema<Object> schema = getSchema(cls);
		ProtostuffIOUtil.mergeFrom(input, message, schema);
		return message;
	}

	@Override
	@SneakyThrows
	public void serialize(OutputStream output, Object object) {
		Class cls = object.getClass();
		@Cleanup("clear")
		LinkedBuffer buffer = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);
		Schema schema = getSchema(cls);
		ProtostuffIOUtil.writeTo(output, object, schema, buffer);
	}
}
