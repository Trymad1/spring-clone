package com.trymad.infrastructure.bean.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.trymad.infrastructure.bean.BeanDefinition;
import com.trymad.infrastructure.bean.BeanDefinitionRegistry;
import com.trymad.infrastructure.exception.BeanDefinitionOverrideException;

public class InMemoryBeanDefinitionBeanRegistry implements BeanDefinitionRegistry {

	final Map<String, BeanDefinition<?>> idRepo;
	final Map<Class<?>, Set<BeanDefinition<?>>> typeRepo;

	private final static boolean ALLOW_BEAN_OVERRIDE = false;

	public InMemoryBeanDefinitionBeanRegistry() {
		this.idRepo = new HashMap<>();
		this.typeRepo = new HashMap<>();
	}

	@SuppressWarnings("unchecked")
	public <T> Set<BeanDefinition<T>> getBeansDefinition(Class<T> clazz) {
		Set<BeanDefinition<?>> beans = typeRepo.getOrDefault(clazz, Set.of());

		Set<BeanDefinition<T>> result = new HashSet<>();

		for (BeanDefinition<?> bean : beans) {
			if (clazz.isAssignableFrom(bean.clazz())) {
				result.add((BeanDefinition<T>) bean);
			}
		}

		return result;
	}

	@Override
	public BeanDefinition<?> getBeanDefinitionById(String id) {
		return idRepo.get(id);
	}

	@Override
	public <T> BeanDefinition<T> registry(BeanDefinition<T> beanDefinition) {
		if (!ALLOW_BEAN_OVERRIDE && idRepo.containsKey(beanDefinition.id())) {
			throw new BeanDefinitionOverrideException(beanDefinition.id());
		}

		idRepo.put(beanDefinition.id(), beanDefinition);

		Set<Class<?>> visited = new HashSet<>();

		Class<?> clazz = beanDefinition.clazz();
		while (clazz != null) {
			registerTypeHierarchy(clazz, beanDefinition, visited);
			clazz = clazz.getSuperclass();
		}

		return beanDefinition;
	}

	private void registerTypeHierarchy(Class<?> type,
									BeanDefinition<?> beanDefinition,
									Set<Class<?>> visited) {

		if (type == null || !visited.add(type)) {
			return;
		}

		typeRepo.computeIfAbsent(type, k -> new HashSet<>())
				.add(beanDefinition);

		for (Class<?> iface : type.getInterfaces()) {
			registerTypeHierarchy(iface, beanDefinition, visited);
		}
	}
	
}
