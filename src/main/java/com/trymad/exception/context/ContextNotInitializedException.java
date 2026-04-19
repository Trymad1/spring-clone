package com.trymad.exception.context;

public class ContextNotInitializedException extends ApplicationContextException {

	public ContextNotInitializedException(String msg, Throwable e) {
		super("ApplicationContext is not initialized.", e);
	}
	
}
