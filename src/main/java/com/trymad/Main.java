package com.trymad;

import java.io.IOException;
import java.net.URISyntaxException;

import com.trymad.infrastructure.Application;
import com.trymad.infrastructure.context.ApplicationContext;

public class Main {
    public static void main( String[] args ) throws IOException, URISyntaxException, ClassNotFoundException {
        final ApplicationContext context = Application.run();
    }
}
