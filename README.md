# spring-clone

`spring-clone` is a small educational dependency injection container inspired by the core ideas of Spring. It scans classes annotated with `@Component`, builds `BeanDefinition` metadata, registers definitions in an in-memory registry, and creates bean instances through an `ApplicationContext`.

The project is intentionally compact and focused on constructor-based dependency injection, which makes it a good playground for understanding how IoC containers work internally.

## What It Supports

- Component scanning through `ClassMetadataScanner`
- Bean registration with unique bean ids
- Constructor-based injection
- Constructor selection via `@Autowired`
- Bean lookup by `id` or by `type`
- `@Qualifier` support for constructor parameters
- `@Value` support for literal constructor arguments
- `@Primary` metadata on bean definitions
- In-memory bean definition registry
- Singleton instance caching inside `AnnotationConfigApplicationContext`

## Main Flow

The default bootstrap path is implemented in [Application.java](d:/code/spring-clone/src/main/java/com/trymad/Application.java):

1. Create a `ClassMetadataScanner`
2. Build `JavaConfiguration`
3. Create `AnnotationConfigApplicationContext`
4. Attach `BeanFactoryImpl`
5. Call `refresh()`
6. Resolve beans from the context

## Architecture

```text
Application
  |
  v
Scanner
  |
  v
JavaConfiguration
  |
  v
BeanDefinitions
  |
  v
ApplicationContext
  | \
  |  \--> Registry
  | \
  |  \--> SingletonStore
  |
  v
BeanLookup
  |
  v
BeanFactory
  |
  v
BeanInstance
  |
  v
SingletonStore
```

## Core Pieces

- `ApplicationContext`
  Exposes `getBean(...)`, `registry(...)`, `setFactory(...)`, and `refresh()`.

- `AnnotationConfigApplicationContext`
  Coordinates bean lookup, singleton caching, and configuration refresh.

- `Configuration`
  Produces `BeanDefinition` metadata for the container.

- `JavaConfiguration`
  Reads `@Component` classes from the scanner and converts them into `BeanDefinition` instances.

- `BeanDefinitionRegistry`
  Stores bean definitions and indexes them by both `id` and type hierarchy.

- `BeanFactory`
  Creates bean instances from `BeanDefinition` metadata.

- `BeanFactoryImpl`
  Resolves constructor arguments from literal values, qualifiers, or type lookups through `ApplicationContext`.

## Supported Annotations

- `@Component`
  Marks a class as a managed bean. If no explicit value is provided, the bean id is derived from `clazz.getSimpleName().toLowerCase()`.

- `@Autowired`
  Marks the constructor that should be used when a component has more than one constructor.

- `@Qualifier`
  Selects a dependency by bean id for a constructor parameter.

- `@Value`
  Injects a literal value into a constructor parameter.

- `@Primary`
  Marks a bean definition as primary in metadata.

## Example

```java
package com.example;

import com.trymad.Application;
import com.trymad.annotation.Autowired;
import com.trymad.annotation.Component;
import com.trymad.context.ApplicationContext;

@Component
class UserRepository {
}

@Component
class UserService {
    private final UserRepository repository;

    @Autowired
    UserService(UserRepository repository) {
        this.repository = repository;
    }
}

public class Demo {
    public static void main(String[] args) {
        ApplicationContext context = Application.run("com.example");
        UserService service = context.getBean(UserService.class);
    }
}
```

## Public API Snapshot

```java
ApplicationContext context = Application.run("com.example");

MyService byType = context.getBean(MyService.class);
Object byId = context.getBean("myService");
MyService typedById = context.getBean("myService", MyService.class);
```

## Running

Requirements:

- Java 21
- Maven 3.9+

Run tests:

```bash
mvn test
```

Build the jar:

```bash
mvn package
```

## Current Limitations

- `Main.java` is currently empty and does not start a demo application by itself
- `@Primary` is stored in `BeanDefinition`, but bean selection by type currently requires exactly one candidate
- There is no `@Bean` method configuration yet
- Field injection is not implemented
- Circular dependency handling is not implemented
- Scope management beyond singleton caching is not implemented
- The default bean id strategy is a simple lowercase transformation of the class simple name

## Tests

The project includes contract-style tests for the main interfaces and implementation-specific tests where needed:

- `BeanDefinitionRegistry`
- `BeanFactory`
- `Configuration`
- `ApplicationContext`

This makes it easier to add new implementations while keeping behavior consistent across the container.
