package com.newlandframework.test;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import com.newlandframework.rpc.core.AbilityDetail;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RunWith(SpringRunner.class)
@ContextConfiguration("classpath:rpc-invoke-config-client.xml")
public class RpcAbilityDetailProviderTest {

	@Resource(name = "ability")
	private AbilityDetail provider;

	@Test
	public void testRpcAbilityDetailProvider() {
		StringBuilder ability = provider.listAbilityDetail(false);
		log.info(ability.toString());
	}
}
