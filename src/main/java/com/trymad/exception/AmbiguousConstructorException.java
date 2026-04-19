package com.trymad.exception;

public class AmbiguousConstructorException extends RuntimeException {
	
	public AmbiguousConstructorException(Class<?> clazz) {
		super("Ambiguous constructors: multiple @Autowired constructors found in " + clazz.getName());
	}

}
