package com.newlandframework.rpc.services.pojo;

import java.io.Serializable;

import lombok.Data;

@Data
@SuppressWarnings("serial")
public class CostTime implements Serializable {
	public long elapse;
	public String detail;
}