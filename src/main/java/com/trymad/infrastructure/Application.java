package com.trymad.infrastructure;

import com.trymad.infrastructure.bean.BeanFactory;
import com.trymad.infrastructure.bean.impl.BeanFactoryImpl;
import com.trymad.infrastructure.config.Configuration;
import com.trymad.infrastructure.config.impl.JavaConfiguration;
import com.trymad.infrastructure.context.ApplicationContext;
import com.trymad.infrastructure.context.impl.AnnotationConfigApplicationContext;
import com.trymad.infrastructure.util.ClassMetadataScanner;
import com.trymad.infrastructure.util.impl.ReflectionsAdapterClassMetadataScanner;

public class Application {

	public static ApplicationContext run() {
		final ClassMetadataScanner scanner = new ReflectionsAdapterClassMetadataScanner("com.trymad.app");
		final Configuration configuration = new JavaConfiguration(scanner);

		final ApplicationContext context = new AnnotationConfigApplicationContext(configuration);
		final BeanFactory factory = new BeanFactoryImpl(context);
		context.setFactory(factory);

		context.refresh();

		return context;
	}
	
}
