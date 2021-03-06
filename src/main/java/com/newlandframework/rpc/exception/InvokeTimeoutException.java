package com.newlandframework.rpc.exception;

@SuppressWarnings("serial")
public class InvokeTimeoutException extends RuntimeException {

	public InvokeTimeoutException() {
		super();
	}

	public InvokeTimeoutException(String message, Throwable cause) {
		super(message, cause);
	}

	public InvokeTimeoutException(String message) {
		super(message);
	}

	public InvokeTimeoutException(Throwable cause) {
		super(cause);
	}
}
