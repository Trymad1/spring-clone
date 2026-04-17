package com.trymad.infrastructure.bean;

import java.util.List;

public record BeanDefinition<T> (
	String id, 
	Class<T> clazz,
	boolean primary,
	List<ConstructorArg<?>> args
	) {
}
