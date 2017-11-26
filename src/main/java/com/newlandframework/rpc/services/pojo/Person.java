package com.newlandframework.rpc.services.pojo;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

@Data
@SuppressWarnings("serial")
public class Person implements Serializable {
	private int id;
	private String name;
	private int age;
	private Date birthday;
}