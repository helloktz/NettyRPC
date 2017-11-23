package com.newlandframework.rpc.services.pojo;

import java.io.Serializable;

import lombok.Data;

@Data
public class CostTime implements Serializable {
	private static final long serialVersionUID = 1L;

	public long elapse;
	public String detail;
}