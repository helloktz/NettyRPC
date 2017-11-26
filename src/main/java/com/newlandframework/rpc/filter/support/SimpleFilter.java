package com.newlandframework.rpc.filter.support;

import java.lang.reflect.Method;

import org.apache.commons.lang3.StringUtils;

import com.newlandframework.rpc.filter.Filter;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SimpleFilter implements Filter {
	@Override
	public boolean before(Method method, Object processor, Object[] requestObjects) {
		log.info(StringUtils.center("[SimpleFilter##before]", 48, "*"));
		return true;
	}

	@Override
	public void after(Method method, Object processor, Object[] requestObjects) {
		log.info(StringUtils.center("[SimpleFilter##after]", 48, "*"));
	}
}
