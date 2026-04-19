package com.trymad.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.trymad.annotation.Autowired;
import com.trymad.annotation.Component;
import com.trymad.annotation.Primary;
import com.trymad.annotation.Qualifier;
import com.trymad.annotation.Value;
import com.trymad.bean.BeanDefinition;
import com.trymad.bean.ConstructorArg;
import com.trymad.exception.AmbiguousConstructorException;
import com.trymad.exception.DuplicateBeanDefinitionException;
import com.trymad.exception.NoSuchBeanConstructorException;
import com.trymad.util.ClassMetadataScanner;

public abstract class ConfigurationContractTest {

	protected ClassMetadataScanner scanner;
	protected Configuration configuration;

	@BeforeEach
	void setUpConfigurationContract() {
		scanner = mock(ClassMetadataScanner.class);
		configuration = createConfiguration(scanner);
	}

	protected abstract Configuration createConfiguration(ClassMetadataScanner scanner);

	@Test
	void loadBeanDefinitionsShouldCreateDefinitionWithDefaultIdAndPrimaryFlag() {
		when(scanner.getAnnotatedWith(Component.class)).thenReturn(Set.of(DefaultNamedPrimaryComponent.class));

		Set<BeanDefinition<?>> definitions = configuration.loadBeanDefinitions();

		assertEquals(1, definitions.size());
		BeanDefinition<?> definition = definitions.iterator().next();
		assertEquals("defaultnamedprimarycomponent", definition.id());
		assertEquals(DefaultNamedPrimaryComponent.class, definition.clazz());
		assertTrue(definition.primary());
		assertTrue(definition.args().isEmpty());
	}

	@Test
	void loadBeanDefinitionsShouldUseExplicitComponentNameAndCollectConstructorArgs() {
		when(scanner.getAnnotatedWith(Component.class)).thenReturn(Set.of(ExplicitNamedComponent.class));

		Set<BeanDefinition<?>> definitions = configuration.loadBeanDefinitions();

		BeanDefinition<?> definition = definitions.iterator().next();
		assertEquals("customBean", definition.id());
		assertFalse(definition.primary());
		assertEquals(2, definition.args().size());

		ConstructorArg<?> firstArg = definition.args().get(0);
		assertEquals(Dependency.class, firstArg.type());
		assertTrue(firstArg.value().isEmpty());
		assertEquals("mainDependency", firstArg.qualifier().orElseThrow());

		ConstructorArg<?> secondArg = definition.args().get(1);
		assertEquals(String.class, secondArg.type());
		assertEquals("${app.name}", secondArg.value().orElseThrow());
		assertTrue(secondArg.qualifier().isEmpty());
	}

	@Test
	void loadBeanDefinitionsShouldUseAutowiredConstructorWhenMultipleConstructorsExist() {
		when(scanner.getAnnotatedWith(Component.class)).thenReturn(Set.of(MultipleConstructorsComponent.class));

		Set<BeanDefinition<?>> definitions = configuration.loadBeanDefinitions();

		BeanDefinition<?> definition = definitions.iterator().next();
		assertEquals(1, definition.args().size());
		assertEquals(Dependency.class, definition.args().get(0).type());
	}

	@Test
	void loadBeanDefinitionsShouldReturnBeanDefinitionsForAllComponentsProvidedByScanner() {
		when(scanner.getAnnotatedWith(Component.class)).thenReturn(Set.of(DefaultNamedPrimaryComponent.class, ExplicitNamedComponent.class));

		Set<BeanDefinition<?>> definitions = configuration.loadBeanDefinitions();

		assertEquals(2, definitions.size());
		assertTrue(definitions.stream().anyMatch(def -> def.id().equals("defaultnamedprimarycomponent")));
		assertTrue(definitions.stream().anyMatch(def -> def.id().equals("customBean")));
		assertTrue(definitions.stream().map(BeanDefinition::clazz).allMatch(clazz -> clazz == DefaultNamedPrimaryComponent.class || clazz == ExplicitNamedComponent.class));
	}

	@Test
	void loadBeanDefinitionsShouldThrowWhenTwoBeansHaveSameId() {
		when(scanner.getAnnotatedWith(Component.class)).thenReturn(Set.of(FirstDuplicateComponent.class, SecondDuplicateComponent.class));

		DuplicateBeanDefinitionException exception = assertThrows(
				DuplicateBeanDefinitionException.class,
				() -> configuration.loadBeanDefinitions()
		);

		assertEquals("Duplicate bean definition with id: duplicateBean", exception.getMessage());
	}

	@Test
	void loadBeanDefinitionsShouldThrowWhenSeveralConstructorsMarkedAutowired() {
		when(scanner.getAnnotatedWith(Component.class)).thenReturn(Set.of(AmbiguousConstructorsComponent.class));

		AmbiguousConstructorException exception = assertThrows(
				AmbiguousConstructorException.class,
				() -> configuration.loadBeanDefinitions()
		);

		assertTrue(exception.getMessage().contains(AmbiguousConstructorsComponent.class.getName()));
	}

	@Test
	void loadBeanDefinitionsShouldThrowWhenNoConstructorCanBeChosen() {
		when(scanner.getAnnotatedWith(Component.class)).thenReturn(Set.of(NoMatchingConstructorComponent.class));

		NoSuchBeanConstructorException exception = assertThrows(
				NoSuchBeanConstructorException.class,
				() -> configuration.loadBeanDefinitions()
		);

		assertTrue(exception.getMessage().contains(NoMatchingConstructorComponent.class.getName()));
	}

	@Component
	@Primary
	protected static final class DefaultNamedPrimaryComponent {
		protected DefaultNamedPrimaryComponent() {
		}
	}

	@Component("customBean")
	protected static final class ExplicitNamedComponent {
		protected ExplicitNamedComponent(@Qualifier("mainDependency") Dependency dependency, @Value("${app.name}") String appName) {
		}
	}

	@Component
	protected static final class MultipleConstructorsComponent {
		protected MultipleConstructorsComponent() {
		}

		@Autowired
		protected MultipleConstructorsComponent(Dependency dependency) {
		}
	}

	protected static final class Dependency {
	}

	@Component("duplicateBean")
	protected static final class FirstDuplicateComponent {
		protected FirstDuplicateComponent() {
		}
	}

	@Component("duplicateBean")
	protected static final class SecondDuplicateComponent {
		protected SecondDuplicateComponent() {
		}
	}

	@Component
	protected static final class AmbiguousConstructorsComponent {
		@Autowired
		protected AmbiguousConstructorsComponent(Dependency dependency) {
		}

		@Autowired
		protected AmbiguousConstructorsComponent(String value) {
		}
	}

	@Component
	protected static final class NoMatchingConstructorComponent {
		protected NoMatchingConstructorComponent() {
		}

		protected NoMatchingConstructorComponent(Dependency dependency) {
		}
	}

}
