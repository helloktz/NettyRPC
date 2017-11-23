package com.newlandframework.rpc.services.pojo;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

@Data
public class Person implements Serializable {
	private static final long serialVersionUID = 1L;

	private int id;
	private String name;
	private int age;
	private Date birthday;
}