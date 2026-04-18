package com.trymad.infrastructure.bean;

import java.util.Optional;

public record ConstructorArg<T>(
	Class<T> type, 
	Optional<Object> value, 
	Optional<String> qualifier) {
	
}
