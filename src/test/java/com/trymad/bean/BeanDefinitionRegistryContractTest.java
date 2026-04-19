package com.trymad.bean;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.trymad.exception.BeanDefinitionOverrideException;

public abstract class BeanDefinitionRegistryContractTest {

	protected BeanDefinitionRegistry registry;

	@BeforeEach
	void setUpRegistryContract() {
		registry = createRegistry();
	}

	protected abstract BeanDefinitionRegistry createRegistry();

	@Test
	void registryShouldReturnSameBeanDefinitionAndStoreItById() {
		BeanDefinition<ServiceImpl> beanDefinition = mockBeanDefinition("serviceBean", ServiceImpl.class);

		BeanDefinition<ServiceImpl> result = registry.registry(beanDefinition);

		assertSame(beanDefinition, result);
		assertTrue(registry.contains("serviceBean"));
		assertSame(beanDefinition, registry.getBeanDefinitionById("serviceBean"));
	}

	@Test
	void getBeansDefinitionShouldReturnBeanForExactType() {
		BeanDefinition<ServiceImpl> beanDefinition = mockBeanDefinition("serviceBean", ServiceImpl.class);
		registry.registry(beanDefinition);

		Set<BeanDefinition<ServiceImpl>> definitions = registry.getBeansDefinition(ServiceImpl.class);

		assertEquals(1, definitions.size());
		assertTrue(definitions.contains(beanDefinition));
	}

	@Test
	void getBeansDefinitionShouldReturnBeanForImplementedInterface() {
		BeanDefinition<ServiceImpl> beanDefinition = mockBeanDefinition("serviceBean", ServiceImpl.class);
		registry.registry(beanDefinition);

		Set<BeanDefinition<BaseContract>> definitions = registry.getBeansDefinition(BaseContract.class);

		assertEquals(1, definitions.size());
		assertTrue(definitions.contains(beanDefinition));
	}

	@Test
	void getBeansDefinitionShouldReturnBeanForSuperclass() {
		BeanDefinition<ServiceImpl> beanDefinition = mockBeanDefinition("serviceBean", ServiceImpl.class);
		registry.registry(beanDefinition);

		Set<BeanDefinition<AbstractService>> definitions = registry.getBeansDefinition(AbstractService.class);

		assertEquals(1, definitions.size());
		assertTrue(definitions.contains(beanDefinition));
	}

	@Test
	void registryShouldThrowWhenBeanWithSameIdAlreadyExists() {
		BeanDefinition<ServiceImpl> firstDefinition = mockBeanDefinition("serviceBean", ServiceImpl.class);
		BeanDefinition<AlternativeService> secondDefinition = mockBeanDefinition("serviceBean", AlternativeService.class);

		registry.registry(firstDefinition);

		BeanDefinitionOverrideException exception = assertThrows(
				BeanDefinitionOverrideException.class,
				() -> registry.registry(secondDefinition)
		);

		assertEquals(
				"The bean serviceBean could not be registered. A bean with that name has already been defined.",
				exception.getMessage()
		);
		assertSame(firstDefinition, registry.getBeanDefinitionById("serviceBean"));
	}

	@Test
	void clearShouldRemoveAllRegisteredBeans() {
		BeanDefinition<ServiceImpl> beanDefinition = mockBeanDefinition("serviceBean", ServiceImpl.class);
		registry.registry(beanDefinition);

		registry.clear();

		assertFalse(registry.contains("serviceBean"));
		assertNull(registry.getBeanDefinitionById("serviceBean"));
		assertTrue(registry.getBeansDefinition(ServiceImpl.class).isEmpty());
		assertTrue(registry.getBeansDefinition(BaseContract.class).isEmpty());
	}

	@Test
	void getBeansDefinitionShouldReturnEmptySetForUnknownType() {
		assertDoesNotThrow(() -> assertTrue(registry.getBeansDefinition(UnusedType.class).isEmpty()));
	}

	@SuppressWarnings("unchecked")
	protected <T> BeanDefinition<T> mockBeanDefinition(String id, Class<T> clazz) {
		BeanDefinition<T> beanDefinition = mock(BeanDefinition.class);
		when(beanDefinition.id()).thenReturn(id);
		when(beanDefinition.clazz()).thenReturn(clazz);
		return beanDefinition;
	}

	protected interface BaseContract {
	}

	protected abstract static class AbstractService implements BaseContract {
	}

	protected static final class ServiceImpl extends AbstractService {
	}

	protected static final class AlternativeService extends AbstractService {
	}

	protected static final class UnusedType {
	}

}
