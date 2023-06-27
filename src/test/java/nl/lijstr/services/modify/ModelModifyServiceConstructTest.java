package nl.lijstr.services.modify;

import lombok.Setter;
import nl.lijstr.services.modify.annotations.NotModifiable;
import nl.lijstr.services.modify.models.ReflectedField;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.test.util.ReflectionTestUtils;

import jakarta.persistence.Entity;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import static nl.lijstr._TestUtils.TestUtils.getInvocationParam;
import static nl.lijstr._TestUtils.TestUtils.mockLogger;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * A Test for {@link ModelModifyService} that mainly focuses on the
 * {@link jakarta.annotation.PostConstruct} method {@link ModelModifyService#construct()}.
 * <p>
 * It uses PowerMockito to override private methods.
 */
@Disabled // TODO: Get this working again
@ExtendWith(MockitoExtension.class)
public class ModelModifyServiceConstructTest {

    public static final String SCANNER_GETTER = "getClassPathScanner";
    private ModelModifyService spyService;
    private Logger mockedLogger;
    private ClassPathScanningCandidateComponentProvider mockProvider;

    @Setter
    private boolean includedEntity = false;
    @Setter
    private boolean excludedNotModifiable = false;

    @BeforeEach
    public void setUp() throws Exception {
        ModelModifyService service = new ModelModifyService();
        mockedLogger = mockLogger(service);
        spyService =  service; // PowerMockito.spy(service);
    }

    @Test
    public void testGetClassPathScanner() {
        //Act
        ClassPathScanningCandidateComponentProvider provider =
                ReflectionTestUtils.invokeMethod(spyService, SCANNER_GETTER);

        //Assert
        assertNotNull(provider);
    }

    @Test
    public void testCallConstruct() throws Exception {
        //Arrange
        overruleProvider();

        Set<BeanDefinition> beanDefinitions = beanDefinitionSet();
        when(mockProvider.findCandidateComponents(any())).thenReturn(beanDefinitions);

        List<ReflectedField> reflectedFields = reflectedFieldList();
//        Method method = MemberMatcher.method(ModelModifyService.class, "loadClassFields", Class.class);
//        PowerMockito.doReturn(reflectedFields).when(spyService, method).withArguments(any(Class.class));

        doAnswer(this::assertLoggerInvocation)
                .when(mockedLogger).info(anyString(), anyInt(), anyString(), anyString());

        //Act
        ReflectionTestUtils.invokeMethod(spyService, "construct");


        //Assert
        assertTrue(excludedNotModifiable);
        assertTrue(includedEntity);
        verify(mockedLogger, times(1)).debug(anyString(), anyInt(), anyString(), anyString());
    }

    private Void assertLoggerInvocation(InvocationOnMock invocation) {
        int fieldsSize = getInvocationParam(invocation, 1);
        assertEquals(1, fieldsSize);

        String className = getInvocationParam(invocation, 2);
        assertEquals(ConstructModel.class.getName(), className);

        return null;
    }

    private Set<BeanDefinition> beanDefinitionSet() {
        Set<BeanDefinition> beanDefinitions = new HashSet<>();
        BeanDefinition beanDefinition = mock(BeanDefinition.class);
        when(beanDefinition.getBeanClassName()).thenReturn(ConstructModel.class.getName());
        beanDefinitions.add(beanDefinition);
        return beanDefinitions;
    }

    private List<ReflectedField> reflectedFieldList() {
        ReflectedField field = mock(ReflectedField.class);
        when(field.getFieldName()).thenReturn("FieldName");
        return Collections.singletonList(field);
    }


    private void overruleProvider() throws Exception {
        mockProvider = mock(ClassPathScanningCandidateComponentProvider.class);

        doAnswer(invocation -> {
            expectedAnnotationFilter(invocation, Entity.class, this::setIncludedEntity);
            return null;
        }).when(mockProvider).addIncludeFilter(any());

        doAnswer(invocation -> {
            expectedAnnotationFilter(invocation, NotModifiable.class, this::setExcludedNotModifiable);
            return null;
        }).when(mockProvider).addExcludeFilter(any());

//        Method method = MemberMatcher.method(ModelModifyService.class, SCANNER_GETTER);
//        PowerMockito.when(spyService, method).withNoArguments().thenReturn(mockProvider);
    }

    private void expectedAnnotationFilter(InvocationOnMock invocation,
                                          Class<? extends Annotation> annotationClass,
                                          Consumer<Boolean> boolSetter) {
        AnnotationTypeFilter typeFilter = getInvocationParam(invocation, 0);
        Class<?> annotationType = (Class<?>) ReflectionTestUtils.getField(typeFilter, "annotationType");
        if (annotationClass.equals(annotationType)) {
            boolSetter.accept(true);
        } else {
            fail();
        }
    }

    public class ConstructModel {
    }

}