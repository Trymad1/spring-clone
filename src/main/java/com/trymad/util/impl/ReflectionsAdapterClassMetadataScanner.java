package com.trymad.util.impl;

import java.lang.annotation.Annotation;
import java.util.Set;

import org.reflections.Reflections;

import com.trymad.util.ClassMetadataScanner;

public class ReflectionsAdapterClassMetadataScanner implements ClassMetadataScanner {

	private final Reflections reflections;

	public ReflectionsAdapterClassMetadataScanner(String packageScan) {
		this.reflections = new Reflections(packageScan);
	}

	@Override
	public <T> Set<Class<? extends T>> getSubtypesOf(Class<T> type) {
		return reflections.getSubTypesOf(type);
	}

	@Override
	public Set<Class<?>> getAnnotatedWith(Class<? extends Annotation> annotation) {
		return reflections.getTypesAnnotatedWith(annotation);
	}
	
}
