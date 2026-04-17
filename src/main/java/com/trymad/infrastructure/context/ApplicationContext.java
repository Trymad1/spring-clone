package com.trymad.infrastructure.context;

public interface ApplicationContext {
	
	<T> T getBean(Class<T> clazz);
	
	Object getBean(String id);
	
	<T> T getBean(String id, Class<T> clazz);

}
