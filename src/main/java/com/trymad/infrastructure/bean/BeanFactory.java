package com.trymad.infrastructure.bean;

public interface BeanFactory {
	
	<T> T create(BeanDefinition<T> definition);

}
