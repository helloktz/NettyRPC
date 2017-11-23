package com.newlandframework.rpc.model;

import java.io.Serializable;

import lombok.Data;
import lombok.ToString;

@ToString(exclude = { "typeParameters", "parametersVal" })
@Data
public class MessageRequest implements Serializable {
	private static final long serialVersionUID = 1L;

	private String messageId;
	private String className;
	private String methodName;
	private Class<?>[] typeParameters;
	private Object[] parametersVal;
	private boolean invokeMetrics = true;

}
