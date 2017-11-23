package com.newlandframework.rpc.exception;

public class RejectResponeException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public RejectResponeException() {
		super();
	}

	public RejectResponeException(String message, Throwable cause) {
		super(message, cause);
	}

	public RejectResponeException(String message) {
		super(message);
	}

	public RejectResponeException(Throwable cause) {
		super(cause);
	}
}
