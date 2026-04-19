package com.trymad;

import com.trymad.bean.BeanFactory;
import com.trymad.bean.impl.BeanFactoryImpl;
import com.trymad.config.Configuration;
import com.trymad.config.impl.JavaConfiguration;
import com.trymad.context.ApplicationContext;
import com.trymad.context.impl.AnnotationConfigApplicationContext;
import com.trymad.util.ClassMetadataScanner;
import com.trymad.util.impl.ReflectionsAdapterClassMetadataScanner;

public class Application {

	public static ApplicationContext run() {
		final ClassMetadataScanner scanner = new ReflectionsAdapterClassMetadataScanner("com.trymad.test");
		final Configuration configuration = new JavaConfiguration(scanner);

		final ApplicationContext context = new AnnotationConfigApplicationContext(configuration);
		final BeanFactory factory = new BeanFactoryImpl(context);
		context.setFactory(factory);

		context.refresh();

		return context;
	}
	
}
