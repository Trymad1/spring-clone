package com.trymad.infrastructure.exception;

public class DuplicateBeanDefinitionException extends RuntimeException {
	
	public DuplicateBeanDefinitionException(String beanId) {
		super("Duplicate bean definition with id: " + beanId);
	}

}
