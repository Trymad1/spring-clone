package com.trymad.infrastructure.exception;

public class NoSuchBeanConstructorException extends RuntimeException {
    public NoSuchBeanConstructorException(Class<?> clazz) {
        super("No suitable constructor found for " + clazz.getName());
    }
}
