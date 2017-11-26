package com.newlandframework.rpc.model;

import java.io.Serializable;

import lombok.Data;
import lombok.ToString;

@Data
@SuppressWarnings("serial")
@ToString(exclude = { "typeParameters", "parametersVal" })
public class MessageRequest implements Serializable {

	private String messageId;
	private String className;
	private String methodName;
	private Class<?>[] typeParameters;
	private Object[] parametersVal;
	private boolean invokeMetrics = true;

}
