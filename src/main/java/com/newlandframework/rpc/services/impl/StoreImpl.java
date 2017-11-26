package com.newlandframework.rpc.services.impl;

import com.newlandframework.rpc.services.Store;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class StoreImpl implements Store {
	@Override
	public void save(String object) {
		log.info("StoreImpl ## save string:[{}]", object);
	}

	@Override
	public void save(int x) {
		log.info("StoreImpl ## save int:[{}]", x);
	}
}
