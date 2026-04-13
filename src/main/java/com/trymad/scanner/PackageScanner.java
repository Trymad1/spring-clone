package com.trymad.scanner;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Set;

public interface PackageScanner {
	
	Set<Class<?>> scan(String packageName) throws IOException, URISyntaxException, ClassNotFoundException;

}
