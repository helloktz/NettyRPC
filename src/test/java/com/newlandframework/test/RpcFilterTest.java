package com.newlandframework.test;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import com.newlandframework.rpc.exception.RejectResponeException;
import com.newlandframework.rpc.services.Cache;
import com.newlandframework.rpc.services.Store;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RunWith(SpringRunner.class)
@ContextConfiguration("classpath:rpc-invoke-config-client.xml")
public class RpcFilterTest {

	@Resource(name = "cache")
	private Cache cache;

	@Resource(name = "store")
	private Store store;

	@Test
	public void testRpcFilter() {

		for (int i = 0; i < 100; i++) {
			String obj = String.valueOf(i);
			try {
				cache.put(obj, obj);
			} catch (RejectResponeException ex) {
				log.error(ex.getMessage(), ex);
			}
		}

		for (int i = 0; i < 100; i++) {
			String obj = String.valueOf(i);
			try {
				log.info((String) cache.get(obj));
				System.out.println((String) cache.get(obj));
			} catch (RejectResponeException ex) {
				log.error(ex.getMessage(), ex);
			}
		}

		for (int i = 0; i < 100; i++) {
			String obj = String.valueOf(i);
			try {
				store.save(obj);
				store.save(i);
			} catch (RejectResponeException ex) {
				log.error(ex.getMessage(), ex);
			}
		}
	}
}
