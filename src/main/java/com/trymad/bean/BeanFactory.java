package com.trymad.bean;

public interface BeanFactory {
	
	<T> T create(BeanDefinition<T> definition);

}
