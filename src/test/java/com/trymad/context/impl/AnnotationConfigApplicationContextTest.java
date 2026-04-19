package com.trymad.context.impl;

import com.trymad.bean.BeanDefinitionRegistry;
import com.trymad.bean.impl.InMemoryBeanDefinitionBeanRegistry;
import com.trymad.config.Configuration;
import com.trymad.context.ApplicationContext;
import com.trymad.context.ApplicationContextContractTest;

class AnnotationConfigApplicationContextTest extends ApplicationContextContractTest {

	@Override
	protected ApplicationContext createContext(Configuration configuration, BeanDefinitionRegistry registry) {
		return new AnnotationConfigApplicationContext(configuration, registry);
	}

	@Override
	protected BeanDefinitionRegistry createRegistry() {
		return new InMemoryBeanDefinitionBeanRegistry();
	}

}
