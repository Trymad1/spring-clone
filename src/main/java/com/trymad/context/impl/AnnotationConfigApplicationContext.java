package com.trymad.context.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.trymad.context.ApplicationContext;
import com.trymad.bean.BeanDefinition;
import com.trymad.bean.BeanDefinitionRegistry;
import com.trymad.bean.BeanFactory;
import com.trymad.bean.impl.InMemoryBeanDefinitionBeanRegistry;
import com.trymad.config.Configuration;
import com.trymad.exception.BeanDefinitionOverrideException;
import com.trymad.exception.NoSuchBeanDefinitionException;
import com.trymad.exception.NoUniqueBeanDefinitionException;
import com.trymad.exception.context.ContextNotInitializedException;

public class AnnotationConfigApplicationContext implements ApplicationContext {

	private final BeanDefinitionRegistry registry;
	private  BeanFactory factory;
	private final Map<String, Object> singletoneStore;
	private final Configuration configuration;

	public AnnotationConfigApplicationContext(Configuration configuration) {
		this(configuration, new InMemoryBeanDefinitionBeanRegistry());
	}

	public AnnotationConfigApplicationContext(Configuration configuration, BeanDefinitionRegistry registry) {
		this.registry = registry;
		this.configuration = configuration;
		this.singletoneStore = new HashMap<>();
	}

	@Override
	public <T> T getBean(Class<T> clazz) {
		final Set<BeanDefinition<T>> definition = registry.getBeansDefinition(clazz);
		if(definition.size() != 1) {
			throw new NoUniqueBeanDefinitionException(clazz);
		}

		return clazz.cast(this.getBean(definition.iterator().next()));
	}

	@Override
	public Object getBean(String id) {
		final BeanDefinition<?> beanDefinition = registry.getBeanDefinitionById(id);
		if(beanDefinition == null) {
			throw new NoSuchBeanDefinitionException(id);
		}
		return this.getBean(beanDefinition);
	}

	@Override
	public <T> T getBean(String id, Class<T> clazz) {
		return clazz.cast(getBean(id));
	}

	@Override
	public BeanFactory setFactory(BeanFactory factory) {
		this.factory = factory;
		return factory;
	}

	@Override
	public void registry(BeanDefinition<?> definition) {
		if(registry.contains(definition.id())) {
			throw new BeanDefinitionOverrideException(definition.id());
		}

		registry.registry(definition);
	}

	private Object getBean(BeanDefinition<?> definition) {
		if(singletoneStore.containsKey(definition.id())) {
			return singletoneStore.get(definition.id());
		}

		if(factory == null) {
			throw new ContextNotInitializedException("ApplicationContext is not initialized.", null);
		}

		final Object bean = factory.create(registry.getBeanDefinitionById(definition.id()));
		singletoneStore.put(definition.id(), bean);
		return bean;
	}

	@Override
	public void refresh() {
		registry.clear();
		singletoneStore.clear();

		final Set<BeanDefinition<?>> definitions = configuration.loadBeanDefinitions();
		definitions.forEach(registry::registry);
	}
	
}
