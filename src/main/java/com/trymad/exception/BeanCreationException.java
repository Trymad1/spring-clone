package com.trymad.exception;

import com.trymad.bean.BeanDefinition;

public class BeanCreationException extends RuntimeException {
	
	public BeanCreationException(BeanDefinition<?> definition, Throwable cause) {
		super("Failed to create bean: " + definition.clazz().getName(), cause);
	}

}
