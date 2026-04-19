package com.trymad.bean.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import com.trymad.bean.BeanFactory;
import com.trymad.bean.BeanFactoryContractTest;
import com.trymad.bean.ConstructorArg;
import com.trymad.context.ApplicationContext;

class BeanFactoryImplTest extends BeanFactoryContractTest {

	@Override
	protected BeanFactory createBeanFactory(ApplicationContext context) {
		return new BeanFactoryImpl(context);
	}

	@Test
	void findConstuctorForArgsShouldReturnMatchingPrivateConstructor() throws NoSuchMethodException {
		BeanFactoryImpl beanFactoryImpl = new BeanFactoryImpl(context);
		List<ConstructorArg<?>> args = List.of(new ConstructorArg<>(String.class, Optional.of("value"), Optional.empty()));
		Constructor<?>[] constructors = PrivateConstructorBean.class.getDeclaredConstructors();

		Optional<Constructor<?>> constructor = beanFactoryImpl.findConstuctorForArgs(args, constructors);

		assertTrue(constructor.isPresent());
		assertEquals(PrivateConstructorBean.class.getDeclaredConstructor(String.class), constructor.get());
		assertTrue(constructor.get().canAccess(null));
	}

	@Test
	void findConstuctorForArgsShouldReturnEmptyWhenTypesDoNotMatch() {
		BeanFactoryImpl beanFactoryImpl = new BeanFactoryImpl(context);
		List<ConstructorArg<?>> args = List.of(new ConstructorArg<>(Integer.class, Optional.of(1), Optional.empty()));
		Constructor<?>[] constructors = ValueOnlyBean.class.getDeclaredConstructors();

		Optional<Constructor<?>> constructor = beanFactoryImpl.findConstuctorForArgs(args, constructors);

		assertTrue(constructor.isEmpty());
	}

}
