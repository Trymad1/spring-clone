package com.trymad.config.impl;

import com.trymad.config.Configuration;
import com.trymad.config.ConfigurationContractTest;
import com.trymad.util.ClassMetadataScanner;

class JavaConfigurationTest extends ConfigurationContractTest {

	@Override
	protected Configuration createConfiguration(ClassMetadataScanner scanner) {
		return new JavaConfiguration(scanner);
	}

}
