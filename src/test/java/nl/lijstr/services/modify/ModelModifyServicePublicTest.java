package nl.lijstr.services.modify;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import nl.lijstr.domain.base.IdModel;
import nl.lijstr.services.modify.models.ReflectedField;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static nl.lijstr.TestUtils.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * A Test for {@link ModelModifyService} that mainly focuses on the public methods.
 */
public class ModelModifyServicePublicTest {

    private ModelModifyService service;
    private FieldModifierService mockFieldModifier;

    private Map<Class<?>, List<ReflectedField>> mirroredClassListMap;

    @SuppressWarnings("unchecked")
    @Before
    public void setUp() throws Exception {
        mockFieldModifier = mock(FieldModifierService.class);

        service = new ModelModifyService();
        mockLogger(service);
        insertMocks(service, mockFieldModifier);
        mirroredClassListMap = (Map<Class<?>, List<ReflectedField>>)
                ReflectionTestUtils.getField(service, "classToReflectedFields");
    }

    @Test
    public void testEmptyModify() throws Exception {
        //Act
        Optional<ModifyClass> optional = service.modify(null, new ModifyClass(), new ModifyClass());

        //Assert
        assertFalse(optional.isPresent());
    }

    @Test
    public void testFilledModify() throws Exception {
        //Arrange
        ModifyClass originalModify = new ModifyClass();
        ModifyClass modifyWith = new ModifyClass();
        mirroredClassListMap.put(ModifyClass.class, new ArrayList<>());
        when(mockFieldModifier.modify(any(), any(), any(), any())).thenReturn(Optional.of(originalModify));

        //Act
        Optional<ModifyClass> optional = service.modify(null, originalModify, modifyWith);

        //Assert
        assertEquals(originalModify, optional.get());
        verify(mockFieldModifier, times(1)).modify(any(), any(), any(), any());
    }

    private class ModifyClass extends IdModel {
    }

}