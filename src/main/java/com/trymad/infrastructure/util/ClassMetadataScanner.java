package com.trymad.infrastructure.util;

import java.lang.annotation.Annotation;
import java.util.Set;

public interface ClassMetadataScanner {
	
    <T> Set<Class<? extends T>> getSubtypesOf(Class<T> type);

    Set<Class<?>> getAnnotatedWith(Class<? extends Annotation> annotation);

}
