package com.trymad.scanner.impl;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import com.trymad.scanner.PackageScanner;

public class SimplePackageScannerImpl implements PackageScanner {

    @Override
    public Set<Class<?>> scan(String packageName)
            throws IOException, URISyntaxException, ClassNotFoundException {

        final ClassLoader loader = Thread.currentThread().getContextClassLoader();
        final String path = packageName.replace('.', '/');

        Enumeration<URL> resources = loader.getResources(path);

        Set<URL> fileUrls = new HashSet<>();
        Set<URL> jarUrls = new HashSet<>();

        while (resources.hasMoreElements()) {
            URL url = resources.nextElement();

            if ("file".equals(url.getProtocol())) {
                fileUrls.add(url);
            } else if ("jar".equals(url.getProtocol())) {
                jarUrls.add(url);
            }
        }

        Set<Class<?>> classes = new HashSet<>();

        classes.addAll(fileSystemScan(packageName, fileUrls, loader));
        classes.addAll(jarScan(packageName, jarUrls, loader));

        return classes;
    }

    private Set<Class<?>> fileSystemScan(String packageName,
                                         Set<URL> urls,
                                         ClassLoader loader)
            throws URISyntaxException, IOException, ClassNotFoundException {

        final Set<Class<?>> classes = new HashSet<>();

        for (URL url : urls) {

            final Path packagePath = Path.of(url.toURI());
            final Queue<Path> directoryQueue = new ArrayDeque<>();
            directoryQueue.add(packagePath);

            while (!directoryQueue.isEmpty()) {
                try (DirectoryStream<Path> dirStream =
                             Files.newDirectoryStream(directoryQueue.poll())) {

                    for (Path curPath : dirStream) {

                        if (Files.isDirectory(curPath)) {
                            directoryQueue.add(curPath);
                            continue;
                        }

                        if (curPath.getFileName().toString().endsWith(".class")) {

                            final Path relativePath = packagePath.relativize(curPath);

                            final String className = relativePath.toString()
                                    .replace('/', '.')
                                    .replace('\\', '.')
                                    .replace(".class", "");

                            classes.add(loader.loadClass(packageName + "." + className));
                        }
                    }
                }
            }
        }

        return classes;
    }

    private Set<Class<?>> jarScan(String packageName,
                                 Set<URL> urls,
                                 ClassLoader loader)
            throws IOException, ClassNotFoundException {

        final Set<Class<?>> classes = new HashSet<>();
        final String path = packageName.replace('.', '/');

        for (URL url : urls) {

            String fullPath = url.getPath();
            String jarPath = fullPath.substring(5, fullPath.indexOf("!"));

            try (JarFile jar = new JarFile(jarPath)) {

                Enumeration<JarEntry> entries = jar.entries();

                while (entries.hasMoreElements()) {
                    JarEntry entry = entries.nextElement();

                    String name = entry.getName();

                    if (name.startsWith(path + "/") && name.endsWith(".class")) {

                        String className = name
                                .replace('/', '.')
                                .replace(".class", "");

                        classes.add(loader.loadClass(className));
                    }
                }
            }
        }

        return classes;
    }
}