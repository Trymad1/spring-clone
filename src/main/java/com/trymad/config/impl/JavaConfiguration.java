package com.trymad.config.impl;

import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.trymad.annotation.Autowired;
import com.trymad.annotation.Component;
import com.trymad.annotation.Primary;
import com.trymad.annotation.Qualifier;
import com.trymad.annotation.Value;
import com.trymad.bean.BeanDefinition;
import com.trymad.bean.ConstructorArg;
import com.trymad.config.Configuration;
import com.trymad.exception.AmbiguousConstructorException;
import com.trymad.exception.DuplicateBeanDefinitionException;
import com.trymad.util.ClassMetadataScanner;

// TODO add method bean config
public class JavaConfiguration implements Configuration {

	final ClassMetadataScanner scanner;

	public JavaConfiguration(ClassMetadataScanner scanner) {
		this.scanner = scanner;
	}

	@Override
	public Set<BeanDefinition<?>> loadBeanDefinitions() {
		final Set<BeanDefinition<?>> definitions = new HashSet<>();
		final Set<String> existedId = new HashSet<>();

		final Set<Class<?>> components = scanner.getAnnotatedWith(Component.class);
		for(Class<?> c : components) {
			final Component component = c.getAnnotation(Component.class);
			final String beanId = component.value().isBlank() ? c.getSimpleName().toLowerCase() : component.value();
			final BeanDefinition<?> beanDefinition = createBeanDefinition(c, beanId);
			if(existedId.contains(beanDefinition.id())) {
				throw new DuplicateBeanDefinitionException(beanDefinition.id());
			}
			
			existedId.add(beanDefinition.id());
			definitions.add(beanDefinition);
		}

		return definitions;
		 
	}

	private BeanDefinition<?> createBeanDefinition(Class<?> clazz, String beanId) {
		final boolean isPrimary = clazz.isAnnotationPresent(Primary.class);
		final Constructor<?>[] constructors = clazz.getDeclaredConstructors();
		
		Constructor<?> constructor = null;

		if(constructors.length == 1) {
			constructor = constructors[0];
		}
		else {
			for(Constructor<?> c : constructors) {
				if(c.isAnnotationPresent(Autowired.class) && constructor == null) {
					constructor = c;
					
				}
				else if(c.isAnnotationPresent(Autowired.class) && constructor != null) {
					throw new AmbiguousConstructorException(clazz);
				}
			}
		}

		final Parameter[] parameters = constructor.getParameters();

		final List<ConstructorArg<?>> args = new ArrayList<>();
		for(Parameter p : parameters) {
			
			String qualifierAnnotationValue = null;
			if(p.isAnnotationPresent(Qualifier.class)) {
				final Qualifier qualifierAnnotation = p.getAnnotation(Qualifier.class);
   				qualifierAnnotationValue = qualifierAnnotation.value();
			}

			String valueAnnotaionValue = null;
			if(p.isAnnotationPresent(Value.class)) {
				final Value valueAnnotation = p.getAnnotation(Value.class);
				valueAnnotaionValue = valueAnnotation.value();
			}

			args.add(new ConstructorArg<>(
				p.getType(), 
				Optional.ofNullable(valueAnnotaionValue), 
				Optional.ofNullable(qualifierAnnotationValue)));
		}

		return new BeanDefinition<>(
			beanId,
			clazz,
			isPrimary,
			args
		);

		
	}
	
}
