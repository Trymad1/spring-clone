package com.trymad.exception;

public class BeanDefinitionOverrideException extends RuntimeException {
	
	public BeanDefinitionOverrideException(String id) {
		super("The bean %s could not be registered. A bean with that name has already been defined.".formatted(id));
	}

}
