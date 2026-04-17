package com.trymad;

import java.io.IOException;
import java.net.URISyntaxException;

import com.trymad.app.Animal;
import com.trymad.infrastructure.annotation.Component;
import com.trymad.infrastructure.util.ClassMetadataScanner;
import com.trymad.infrastructure.util.impl.ReflectionsAdapterClassMetadataScanner;

public class Main {
    public static void main( String[] args ) throws IOException, URISyntaxException, ClassNotFoundException {
        final ClassMetadataScanner scanner = new ReflectionsAdapterClassMetadataScanner("com.trymad.app");
        System.out.println(scanner.getSubtypesOf(Animal.class));
        System.out.println(scanner.getAnnotatedWith(Component.class));
    }
}
