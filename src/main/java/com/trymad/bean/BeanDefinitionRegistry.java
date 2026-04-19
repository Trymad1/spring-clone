package com.trymad.bean;

import java.util.Set;

public interface BeanDefinitionRegistry {

	<T> BeanDefinition<T> registry(BeanDefinition<T> beanDefinition);

	<T> Set<BeanDefinition<T>> getBeansDefinition(Class<T> clazz);

	BeanDefinition<?> getBeanDefinitionById(String id);

	boolean contains(String id);

	void clear();

}
