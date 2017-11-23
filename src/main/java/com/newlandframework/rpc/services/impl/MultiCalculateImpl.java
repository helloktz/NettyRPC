package com.newlandframework.rpc.services.impl;

import com.newlandframework.rpc.services.MultiCalculate;

public class MultiCalculateImpl implements MultiCalculate {
	// 两数相乘
	@Override
	public int multi(int a, int b) {
		return a * b;
	}
}
