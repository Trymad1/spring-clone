package com.trymad.exception;

public class NoSuchBeanDefinitionException extends RuntimeException {

	public NoSuchBeanDefinitionException(Class<?> clazz) {
		super("No such bean definition for " + clazz.getName());
	}

	public NoSuchBeanDefinitionException(String id) {
		super("No such bean definition for id: " + id);
	}

}
