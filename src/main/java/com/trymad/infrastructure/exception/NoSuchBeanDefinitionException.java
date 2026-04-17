package com.trymad.infrastructure.exception;

public class NoSuchBeanDefinitionException extends RuntimeException {

	public NoSuchBeanDefinitionException(Class<?> clazz) {
		super("No such bean definition for " + clazz.getName());
	}

}
