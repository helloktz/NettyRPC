package com.newlandframework.rpc.exception;

public class CreateProxyException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public CreateProxyException() {
		super();
	}

	public CreateProxyException(String message, Throwable cause) {
		super(message, cause);
	}

	public CreateProxyException(String message) {
		super(message);
	}

	public CreateProxyException(Throwable cause) {
		super(cause);
	}
}
