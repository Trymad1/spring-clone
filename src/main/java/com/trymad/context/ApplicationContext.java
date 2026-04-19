package com.trymad.context;

import com.trymad.bean.BeanDefinition;
import com.trymad.bean.BeanFactory;

public interface ApplicationContext {
	
	<T> T getBean(Class<T> clazz);
	
	Object getBean(String id);
	
	<T> T getBean(String id, Class<T> clazz);

	void registry(BeanDefinition<?> definition);

	BeanFactory setFactory(BeanFactory factory);

	void refresh();

}
