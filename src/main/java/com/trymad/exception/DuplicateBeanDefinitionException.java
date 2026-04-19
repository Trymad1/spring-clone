package com.trymad.exception;

public class DuplicateBeanDefinitionException extends RuntimeException {
	
	public DuplicateBeanDefinitionException(String beanId) {
		super("Duplicate bean definition with id: " + beanId);
	}

}
