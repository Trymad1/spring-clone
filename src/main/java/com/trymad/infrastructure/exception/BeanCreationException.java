package com.trymad.infrastructure.exception;

import com.trymad.infrastructure.bean.BeanDefinition;

public class BeanCreationException extends RuntimeException {
	
	public BeanCreationException(BeanDefinition<?> definition, Throwable cause) {
		super("Failed to create bean: " + definition.clazz().getName(), cause);
	}

}
