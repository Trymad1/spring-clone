package com.trymad.infrastructure.config;
import java.util.Set;

import com.trymad.infrastructure.bean.BeanDefinition;

public interface Configuration {
	
	Set<BeanDefinition<?>> loadBeanDefinitions();

}
