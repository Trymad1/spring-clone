package com.trymad.infrastructure.context;

import com.trymad.infrastructure.bean.BeanDefinition;
import com.trymad.infrastructure.bean.BeanFactory;

public interface ApplicationContext {
	
	<T> T getBean(Class<T> clazz);
	
	Object getBean(String id);
	
	<T> T getBean(String id, Class<T> clazz);

	void registry(BeanDefinition<?> definition);

	BeanFactory setFactory(BeanFactory factory);

	void refresh();

}
