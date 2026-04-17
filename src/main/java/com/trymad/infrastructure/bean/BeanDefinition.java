package com.trymad.infrastructure.bean;

public record BeanDefinition<T> (
	String id, 
	Class<T> clazz,
	boolean primary
	) {
}
