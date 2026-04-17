package com.trymad.infrastructure.bean;

import java.util.Optional;

public record ConstructorArg<T>(
	Class<T> clazz, 
	Optional<Object> value, 
	Optional<String> qualifier) {
	
}
