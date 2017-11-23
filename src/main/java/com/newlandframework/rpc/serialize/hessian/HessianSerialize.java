package com.newlandframework.rpc.serialize.hessian;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.caucho.hessian.io.Hessian2Input;
import com.caucho.hessian.io.Hessian2Output;
import com.newlandframework.rpc.serialize.RpcSerialize;

import lombok.Cleanup;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class HessianSerialize implements RpcSerialize {

	@Override
	public void serialize(OutputStream output, Object object) {
		try {
			@Cleanup
			Hessian2Output ho = new Hessian2Output(output);
			ho.startMessage();
			ho.writeObject(object);
			ho.completeMessage();
		} catch (IOException e) {
			log.error(e);
		}
	}

	@Override
	public Object deserialize(InputStream input) {
		Object result = null;
		try {
			@Cleanup
			Hessian2Input hi = new Hessian2Input(input);
			hi.startMessage();
			result = hi.readObject();
			hi.completeMessage();
		} catch (IOException e) {
			log.error(e);
		}
		return result;
	}
}
