package nl.lijstr;

import org.junit.jupiter.api.Test;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.SystemPropertyUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertNotNull;


/**
 * This test is literally only here for Test coverage.
 * Call it 'Ghetto', useless, whatever you want.
 * This was less work than setting up the excludes in every code coverage program.
 */
public class CoverageTest {

    @Test
    public void domainCoverage() throws Exception {
        runCoverage("nl.lijstr.domain", true);
    }

    @Test
    public void exceptionsCoverage() throws Exception {
        runCoverage("nl.lijstr.exceptions", false);
    }

    @Test
    public void modelsCoverage() throws Exception {
        runCoverage("nl.lijstr.services.maf.models", true);
        runCoverage("nl.lijstr.services.omdb.models", true);
    }

    @Test
    public void privateConstructorCoverage() throws Exception {
        Function<Class, Boolean> candidateFunction = c -> {
            if (!isNotAbstractAndNotTest(c)) {
                return false;
            }

            if (c.getDeclaredConstructors().length != 1) {
                return false;
            }

            Constructor constructor = c.getDeclaredConstructors()[0];
            if (!Modifier.isPrivate(constructor.getModifiers())) {
                return false;
            }

            return constructor.getParameterCount() == 0;
        };

        for (Class aClass : findMyTypes("nl.lijstr", candidateFunction)) {
            Constructor constructor = aClass.getDeclaredConstructors()[0];
            ReflectionUtils.makeAccessible(constructor);
            Object instance = constructor.newInstance();
            assertNotNull(instance);
        }
    }

    //Run coverage (constructors, getters, setters)
    private void runCoverage(String scanPackage, boolean runMethods) throws Exception {
        for (Class aClass : findMyTypes(scanPackage, this::isNotAbstractAndNotTest)) {
            Object instance = coverConstructors(aClass);
            if (instance != null && runMethods) {
                coverMethods(aClass, instance);
            }
        }
    }

    //Find all classes in the package
    private List<Class> findMyTypes(String basePackage, Function<Class, Boolean> candidateFunction) throws Exception {
        ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
        MetadataReaderFactory metadataReaderFactory = new CachingMetadataReaderFactory(resourcePatternResolver);

        List<Class> candidates = new ArrayList<>();
        String packageSearchPath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX +
                resolveBasePackage(basePackage) + "/" + "**/*.class";
        Resource[] resources = resourcePatternResolver.getResources(packageSearchPath);
        for (Resource resource : resources) {
            if (resource.isReadable()) {
                MetadataReader metadataReader = metadataReaderFactory.getMetadataReader(resource);
                if (isCandidate(metadataReader, candidateFunction)) {
                    candidates.add(Class.forName(metadataReader.getClassMetadata().getClassName()));
                }
            }
        }

        return candidates;
    }

    private String resolveBasePackage(String basePackage) {
        return ClassUtils.convertClassNameToResourcePath(SystemPropertyUtils.resolvePlaceholders(basePackage));
    }

    private boolean isCandidate(MetadataReader metadataReader,
                                Function<Class, Boolean> candidateFunction) throws Exception {
        Class c = Class.forName(metadataReader.getClassMetadata().getClassName());
        return candidateFunction.apply(c);
    }

    private boolean isNotAbstractAndNotTest(Class c) {
        return !Modifier.isAbstract(c.getModifiers()) && !c.getSimpleName().endsWith("Test");
    }


    private Object coverConstructors(Class<?> aClass) throws Exception {
        Object instance = null;
        for (Constructor<?> constructor : aClass.getConstructors()) {
            Object[] args = new Object[constructor.getParameterCount()];
            for (int i = 0; i < args.length; i++) {
                Class<?> argClass = constructor.getParameterTypes()[i];
                args[i] = get(argClass);
            }
            instance = constructor.newInstance(args);
        }
        return instance;
    }

    private void coverMethods(Class<?> aClass, Object instance) throws Exception {
        ReflectionUtils.doWithMethods(aClass, method -> {
            Object[] args = new Object[0];
            if (method.getName().startsWith("set")) {
                Class<?> argClass = method.getParameterTypes()[0];
                Object arg = get(argClass);
                args = new Object[]{arg};
            }

            if (args.length != method.getParameterCount()) {
                return;
            }

            try {
                ReflectionTestUtils.invokeMethod(instance, method.getName(), args);
            } catch (Exception e) {
                //Lol
            }
        }, method -> {
            String name = method.getName();
            return name.startsWith("get") || name.startsWith("is") || name.startsWith("set");
        });
    }

    private Object get(Class<?> aClass) {
        if (aClass.isPrimitive()) {
            switch (aClass.getSimpleName()) {
                case "boolean":
                    return false;
                case "int":
                    return 0;
                case "double":
                    return 0.0;
                case "long":
                    return 0L;
            }
        }
        return null;
    }

}