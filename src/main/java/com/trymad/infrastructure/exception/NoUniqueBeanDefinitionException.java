package com.trymad.infrastructure.exception;

public class NoUniqueBeanDefinitionException extends RuntimeException {
	
	public NoUniqueBeanDefinitionException(Class<?> clazz) {
		super("No unique bean definition for " + clazz.getName());
	}

}
