package com.trymad.context;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.trymad.bean.BeanDefinition;
import com.trymad.bean.BeanDefinitionRegistry;
import com.trymad.bean.BeanFactory;
import com.trymad.config.Configuration;
import com.trymad.exception.BeanDefinitionOverrideException;
import com.trymad.exception.NoSuchBeanDefinitionException;
import com.trymad.exception.NoUniqueBeanDefinitionException;
import com.trymad.exception.context.ContextNotInitializedException;

public abstract class ApplicationContextContractTest {

	protected Configuration configuration;
	protected BeanFactory factory;
	protected BeanDefinitionRegistry registry;
	protected ApplicationContext context;

	@BeforeEach
	void setUpApplicationContextContract() {
		configuration = mock(Configuration.class);
		factory = mock(BeanFactory.class);
		registry = createRegistry();
		context = createContext(configuration, registry);
	}

	protected abstract ApplicationContext createContext(Configuration configuration, BeanDefinitionRegistry registry);

	protected abstract BeanDefinitionRegistry createRegistry();

	@Test
	void setFactoryShouldReturnAssignedFactory() {
		BeanFactory assigned = context.setFactory(factory);

		assertSame(factory, assigned);
	}

	@Test
	void registryShouldRegisterBeanDefinitionAndResolveBeanById() {
		context.setFactory(factory);
		BeanDefinition<TestService> definition = beanDefinition("testService", TestService.class);
		TestService service = new TestService("created");
		when(factory.create(definition)).thenReturn(service);

		context.registry(definition);

		assertSame(service, context.getBean("testService", TestService.class));
		verify(factory).create(definition);
	}

	@Test
	void getBeanByClassShouldResolveUniqueDefinition() {
		context.setFactory(factory);
		BeanDefinition<TestService> definition = beanDefinition("testService", TestService.class);
		TestService service = new TestService("typed");
		when(factory.create(definition)).thenReturn(service);
		context.registry(definition);

		TestService result = context.getBean(TestService.class);

		assertSame(service, result);
		verify(factory).create(definition);
	}

	@Test
	void getBeanShouldCreateSingletonOnlyOnce() {
		context.setFactory(factory);
		BeanDefinition<TestService> definition = beanDefinition("testService", TestService.class);
		TestService service = new TestService("singleton");
		when(factory.create(definition)).thenReturn(service);
		context.registry(definition);

		Object first = context.getBean("testService");
		Object second = context.getBean("testService");

		assertSame(first, second);
		verify(factory, times(1)).create(definition);
	}

	@Test
	void refreshShouldLoadDefinitionsFromConfigurationAndRecreateBeansAfterCacheClear() {
		context.setFactory(factory);
		BeanDefinition<TestService> definition = beanDefinition("testService", TestService.class);
		when(configuration.loadBeanDefinitions()).thenReturn(Set.of(definition));

		TestService firstBean = new TestService("first");
		TestService secondBean = new TestService("second");
		when(factory.create(definition)).thenReturn(firstBean, secondBean);

		context.refresh();
		TestService beforeSecondRefresh = context.getBean("testService", TestService.class);
		context.refresh();
		TestService afterSecondRefresh = context.getBean("testService", TestService.class);

		assertEquals("first", beforeSecondRefresh.name);
		assertEquals("second", afterSecondRefresh.name);
		verify(configuration, times(2)).loadBeanDefinitions();
		verify(factory, times(2)).create(definition);
	}

	@Test
	void refreshShouldReplacePreviouslyRegisteredDefinitions() {
		context.setFactory(factory);
		BeanDefinition<TestService> oldDefinition = beanDefinition("oldService", TestService.class);
		BeanDefinition<TestService> newDefinition = beanDefinition("newService", TestService.class);
		context.registry(oldDefinition);
		when(configuration.loadBeanDefinitions()).thenReturn(Set.of(newDefinition));

		context.refresh();

		NoSuchBeanDefinitionException exception = assertThrows(
				NoSuchBeanDefinitionException.class,
				() -> context.getBean("oldService")
		);

		assertTrue(exception.getMessage().contains("oldService"));
	}

	@Test
	void registryShouldThrowWhenBeanWithSameIdAlreadyExists() {
		context.setFactory(factory);
		BeanDefinition<TestService> first = beanDefinition("testService", TestService.class);
		BeanDefinition<AlternativeService> second = beanDefinition("testService", AlternativeService.class);
		context.registry(first);

		BeanDefinitionOverrideException exception = assertThrows(
				BeanDefinitionOverrideException.class,
				() -> context.registry(second)
		);

		assertTrue(exception.getMessage().contains("testService"));
	}

	@Test
	void getBeanByClassShouldThrowWhenNoDefinitionsExist() {
		context.setFactory(factory);

		NoUniqueBeanDefinitionException exception = assertThrows(
				NoUniqueBeanDefinitionException.class,
				() -> context.getBean(TestService.class)
		);

		assertTrue(exception.getMessage().contains(TestService.class.getName()));
	}

	@Test
	void getBeanByClassShouldThrowWhenSeveralDefinitionsExist() {
		context.setFactory(factory);
		context.registry(beanDefinition("firstService", FirstService.class));
		context.registry(beanDefinition("secondService", SecondService.class));

		NoUniqueBeanDefinitionException exception = assertThrows(
				NoUniqueBeanDefinitionException.class,
				() -> context.getBean(BaseService.class)
		);

		assertTrue(exception.getMessage().contains(BaseService.class.getName()));
	}

	@Test
	void getBeanShouldThrowWhenFactoryWasNotInitialized() {
		BeanDefinition<TestService> definition = beanDefinition("testService", TestService.class);
		context.registry(definition);

		ContextNotInitializedException exception = assertThrows(
				ContextNotInitializedException.class,
				() -> context.getBean("testService")
		);

		assertTrue(exception.getMessage().contains("ApplicationContext is not initialized."));
	}

	protected <T> BeanDefinition<T> beanDefinition(String id, Class<T> clazz) {
		return new BeanDefinition<>(id, clazz, false, List.of());
	}

	protected interface BaseService {
	}

	protected static class TestService implements BaseService {
		final String name;

		protected TestService(String name) {
			this.name = name;
		}
	}

	protected static final class AlternativeService implements BaseService {
	}

	protected static final class FirstService implements BaseService {
	}

	protected static final class SecondService implements BaseService {
	}

}
