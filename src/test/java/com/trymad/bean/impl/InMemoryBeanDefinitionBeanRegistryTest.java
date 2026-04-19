package com.trymad.bean.impl;

import com.trymad.bean.BeanDefinitionRegistry;
import com.trymad.bean.BeanDefinitionRegistryContractTest;

class InMemoryBeanDefinitionBeanRegistryTest extends BeanDefinitionRegistryContractTest {

	@Override
	protected BeanDefinitionRegistry createRegistry() {
		return new InMemoryBeanDefinitionBeanRegistry();
	}

}
