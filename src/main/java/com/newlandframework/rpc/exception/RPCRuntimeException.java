package com.newlandframework.rpc.exception;

@SuppressWarnings("serial")
public class RPCRuntimeException extends RuntimeException {

	public RPCRuntimeException() {
		super();
	}

	public RPCRuntimeException(String message, Throwable cause) {
		super(message, cause);
	}

	public RPCRuntimeException(String message) {
		super(message);
	}

	public RPCRuntimeException(Throwable cause) {
		super(cause);
	}
}
