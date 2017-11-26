package com.newlandframework.rpc.model;

import java.io.Serializable;

import lombok.Data;

@Data
@SuppressWarnings("serial")
public class MessageResponse implements Serializable {

	private String messageId;
	private String error;
	private Object result;
	private boolean returnNotNull;
}
