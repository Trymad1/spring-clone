package com.trymad.bean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.trymad.context.ApplicationContext;
import com.trymad.exception.BeanCreationException;
import com.trymad.exception.NoSuchBeanConstructorException;

public abstract class BeanFactoryContractTest {

	protected ApplicationContext context;
	protected BeanFactory beanFactory;

	@BeforeEach
	void setUpBeanFactoryContract() {
		context = mock(ApplicationContext.class);
		beanFactory = createBeanFactory(context);
	}

	protected abstract BeanFactory createBeanFactory(ApplicationContext context);

	@Test
	void createShouldInstantiateBeanUsingLiteralValueArgument() {
		BeanDefinition<ValueOnlyBean> definition = new BeanDefinition<>(
				"valueOnlyBean",
				ValueOnlyBean.class,
				false,
				List.of(new ConstructorArg<>(String.class, Optional.of("test-name"), Optional.empty()))
		);

		ValueOnlyBean bean = beanFactory.create(definition);

		assertEquals("test-name", bean.name);
		verifyNoInteractions(context);
	}

	@Test
	void createShouldResolveArgumentByQualifierFromContext() {
		Dependency dependency = new Dependency("qualified");
		when(context.getBean("qualifiedDependency")).thenReturn(dependency);

		BeanDefinition<QualifierBean> definition = new BeanDefinition<>(
				"qualifierBean",
				QualifierBean.class,
				false,
				List.of(new ConstructorArg<>(Dependency.class, Optional.empty(), Optional.of("qualifiedDependency")))
		);

		QualifierBean bean = beanFactory.create(definition);

		assertSame(dependency, bean.dependency);
		verify(context).getBean("qualifiedDependency");
	}

	@Test
	void createShouldResolveArgumentByTypeFromContext() {
		Dependency dependency = new Dependency("typed");
		when(context.getBean(Dependency.class)).thenReturn(dependency);

		BeanDefinition<TypeBean> definition = new BeanDefinition<>(
				"typeBean",
				TypeBean.class,
				false,
				List.of(new ConstructorArg<>(Dependency.class, Optional.empty(), Optional.empty()))
		);

		TypeBean bean = beanFactory.create(definition);

		assertSame(dependency, bean.dependency);
		verify(context).getBean(Dependency.class);
	}

	@Test
	void createShouldPreferLiteralValueOverQualifierAndTypeLookups() {
		BeanDefinition<ValuePriorityBean> definition = new BeanDefinition<>(
				"valuePriorityBean",
				ValuePriorityBean.class,
				false,
				List.of(new ConstructorArg<>(String.class, Optional.of("from-value"), Optional.of("ignoredBean")))
		);

		ValuePriorityBean bean = beanFactory.create(definition);

		assertEquals("from-value", bean.value);
		verifyNoInteractions(context);
	}

	@Test
	void createShouldInstantiateBeanWithPrivateConstructor() {
		BeanDefinition<PrivateConstructorBean> definition = new BeanDefinition<>(
				"privateConstructorBean",
				PrivateConstructorBean.class,
				false,
				List.of(new ConstructorArg<>(String.class, Optional.of("secret"), Optional.empty()))
		);

		PrivateConstructorBean bean = beanFactory.create(definition);

		assertEquals("secret", bean.value);
	}

	@Test
	void createShouldThrowWhenNoMatchingConstructorExists() {
		BeanDefinition<ValueOnlyBean> definition = new BeanDefinition<>(
				"valueOnlyBean",
				ValueOnlyBean.class,
				false,
				List.of(new ConstructorArg<>(Integer.class, Optional.of(42), Optional.empty()))
		);

		NoSuchBeanConstructorException exception = assertThrows(
				NoSuchBeanConstructorException.class,
				() -> beanFactory.create(definition)
		);

		assertTrue(exception.getMessage().contains(ValueOnlyBean.class.getName()));
	}

	@Test
	void createShouldWrapConstructorFailureIntoBeanCreationException() {
		BeanDefinition<FailingBean> definition = new BeanDefinition<>(
				"failingBean",
				FailingBean.class,
				false,
				List.of(new ConstructorArg<>(String.class, Optional.of("boom"), Optional.empty()))
		);

		BeanCreationException exception = assertThrows(
				BeanCreationException.class,
				() -> beanFactory.create(definition)
		);

		assertTrue(exception.getMessage().contains(FailingBean.class.getName()));
		assertInstanceOf(IllegalStateException.class, exception.getCause());
		assertEquals("ctor failed: boom", exception.getCause().getMessage());
	}

	protected static final class Dependency {
		private final String id;

		public Dependency(String id) {
			this.id = id;
		}
	}

	protected static final class ValueOnlyBean {
		final String name;

		public ValueOnlyBean(String name) {
			this.name = name;
		}
	}

	protected static final class QualifierBean {
		final Dependency dependency;

		public QualifierBean(Dependency dependency) {
			this.dependency = dependency;
		}
	}

	protected static final class TypeBean {
		final Dependency dependency;

		public TypeBean(Dependency dependency) {
			this.dependency = dependency;
		}
	}

	protected static final class ValuePriorityBean {
		final String value;

		public ValuePriorityBean(String value) {
			this.value = value;
		}
	}

	protected static final class PrivateConstructorBean {
		final String value;

		private PrivateConstructorBean(String value) {
			this.value = value;
		}
	}

	protected static final class FailingBean {
		public FailingBean(String value) {
			throw new IllegalStateException("ctor failed: " + value);
		}
	}

}
