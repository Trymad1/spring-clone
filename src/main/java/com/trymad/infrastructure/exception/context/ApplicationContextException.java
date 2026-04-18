package com.trymad.infrastructure.exception.context;

public class ApplicationContextException extends RuntimeException {
	
	public ApplicationContextException(String msg, Throwable e) {
		super(msg, e);
	}

}
