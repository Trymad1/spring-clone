package com.trymad.config;
import java.util.Set;

import com.trymad.bean.BeanDefinition;

public interface Configuration {
	
	Set<BeanDefinition<?>> loadBeanDefinitions();

}
