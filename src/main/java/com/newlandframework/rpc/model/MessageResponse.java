package com.newlandframework.rpc.model;

import java.io.Serializable;

import lombok.Data;

@Data
public class MessageResponse implements Serializable {
	private static final long serialVersionUID = 1L;

	private String messageId;
	private String error;
	private Object result;
	private boolean returnNotNull;
}
