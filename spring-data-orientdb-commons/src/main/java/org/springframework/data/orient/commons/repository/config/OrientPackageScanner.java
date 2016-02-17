package org.springframework.data.orient.commons.repository.config;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.ManagedSet;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.data.orient.commons.repository.annotation.Edge;
import org.springframework.data.orient.commons.repository.annotation.Vertex;

import java.util.HashSet;
import java.util.Set;

/**
 * @author saljuama
 * @since 16th January 2016
 */
public class OrientPackageScanner {

    public static Set<? extends Class<?>> scanBasePackagesForClasses(String... basePackages) throws ClassNotFoundException {
        Set<Class<?>> classes = new HashSet<>();
        for (String basePackage : basePackages)
            classes.addAll( scanBasePackageForClasses(basePackage) );
        return classes;
    }

    public static Set<? extends Class<?>> scanBasePackagesForClasses(Class<?>... basePackageClasses) throws ClassNotFoundException {
        String[] basePackages = new String[basePackageClasses.length];
        for (int i = 0; i < basePackageClasses.length; i++)
            basePackages[i] = basePackageClasses[i].getPackage().getName();
        return scanBasePackagesForClasses(basePackages);
    }


    private static Set<? extends Class<?>> scanBasePackageForClasses(String basePackage) throws ClassNotFoundException {
        Set<Class<?>> classes = new HashSet<>();
        for (String className : scanBasePackage(basePackage))
            classes.add(loadClass(className));
        return classes;
    }

    private static Set<String> scanBasePackage(String basePackage) {
        Set<String> classes = new ManagedSet<>();
        for (BeanDefinition candidate : scanner().findCandidateComponents(basePackage))
            classes.add(candidate.getBeanClassName());
        return classes;
    }

    private static ClassPathScanningCandidateComponentProvider scanner() {
        ClassPathScanningCandidateComponentProvider scanner =
                new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter( new AnnotationTypeFilter(Vertex.class) );
        scanner.addIncludeFilter( new AnnotationTypeFilter(Edge.class) );
        return scanner;
    }

    private static Class loadClass(String className) throws ClassNotFoundException {
        return Thread.currentThread().getContextClassLoader().loadClass(className);
    }
}
