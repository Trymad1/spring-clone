package com.trymad.infrastructure.bean.impl;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Optional;

import com.trymad.infrastructure.bean.BeanDefinition;
import com.trymad.infrastructure.bean.BeanFactory;
import com.trymad.infrastructure.bean.ConstructorArg;
import com.trymad.infrastructure.context.ApplicationContext;
import com.trymad.infrastructure.exception.BeanCreationException;
import com.trymad.infrastructure.exception.NoSuchBeanConstructorException;

public class BeanFactoryImpl implements BeanFactory {

	private final ApplicationContext context;

	public BeanFactoryImpl(ApplicationContext context) {
		this.context = context;
		
	}

	@Override
	public <T> T create(BeanDefinition<T> definition) {
		final List<ConstructorArg<?>> args = definition.args();
		final Class<T> clazz = definition.clazz();
		final Constructor<?>[] constructors = clazz.getDeclaredConstructors();
		
		final Constructor<?> constructor = findConstuctorForArgs(args, constructors)
			.orElseThrow(() -> new NoSuchBeanConstructorException(clazz));
		


		if(constructor == null) throw new NoSuchBeanConstructorException(clazz);
		final Object[] argsForObj = new Object[args.size()];
		for(int i = 0; i < argsForObj.length; i++) {
			final ConstructorArg<?> arg = args.get(i);
			if(arg.value().isPresent()) {
				argsForObj[i] = arg.value().get();
			}
			else if(arg.qualifier().isPresent()) {
				argsForObj[i] = context.getBean(arg.qualifier().get());
			}
			else {
				argsForObj[i] = context.getBean(arg.type());
			}
		}

		try {
			return clazz.cast(constructor.newInstance(argsForObj));
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			throw new BeanCreationException(definition, e.getCause());
		}
	}

	public Optional<Constructor<?>> findConstuctorForArgs(List<ConstructorArg<?>> args, Constructor<?>[] constructors) {
		Constructor<?> constructor = null;
		for(Constructor<?> c : constructors) {
			if(Modifier.isPrivate(c.getModifiers())) {
				c.setAccessible(true);
			}

			if(c.getParameterCount() == args.size()) {
				final Class<?>[] params = c.getParameterTypes();
				boolean isCorrectTypes = true;
				for(int i = 0; i < args.size(); i++) {
					if(!args.get(i).type().equals(params[i])) {
						isCorrectTypes = false;
						break;
					}
				}

				if(isCorrectTypes) {
					constructor = c;
				}
			}
		}

		return Optional.ofNullable(constructor);
	}	
}
