package nl.lijstr.services.modify;

import java.util.*;
import javax.persistence.*;
import lombok.*;
import nl.lijstr.services.modify.annotations.ExternalModifiable;
import nl.lijstr.services.modify.annotations.ModifiableWithHistory;
import nl.lijstr.services.modify.annotations.NotModifiable;
import nl.lijstr.services.modify.models.ReflectedField;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static nl.lijstr._TestUtils.TestUtils.*;
import static org.junit.Assert.*;

/**
 * A Test for {@link ModelModifyService} that mainly focuses on the
 * method {@link ModelModifyService#loadClassFields(Class)}.
 */
public class ModelModifyServiceLoadFieldsTest {

    private ModelModifyService service;

    private Map<Class<?>, List<ReflectedField>> mirroredClassListMap;

    @SuppressWarnings("unchecked")
    @Before
    public void setUp() throws Exception {
        service = new ModelModifyService();
        mockLogger(service);

        mirroredClassListMap = (Map<Class<?>, List<ReflectedField>>)
                ReflectionTestUtils.getField(service, "classToReflectedFields");
        assertEquals(0, mirroredClassListMap.size());
    }

    @Test
    public void testLoadWithNotModifiable() throws Exception {
        //Arrange
        Set<String> expectedFields = new HashSet<>(Arrays.asList("externalModifiable", "modifiable"));

        //Act
        List<ReflectedField> fields = invokeLoadWithAssert(NotModifiableFieldModel.class, 2);

        //Assert
        for (ReflectedField field : fields) {
            assertFalse(field.keepsHistory());

            String fieldName = field.getFieldName();
            assertTrue(expectedFields.contains(fieldName));
            expectedFields.remove(fieldName);
        }

        assertTrue(expectedFields.isEmpty());
    }

    @Test
    public void testLoadWithHistoryModel() throws Exception {
        //Act
        List<ReflectedField> fields = invokeLoadWithAssert(WithHistoryModel.class, 1);
        ReflectedField field = fields.get(0);

        //Assert
        assertEquals("randomVar", field.getFieldName());
        assertTrue(field.keepsHistory());
    }

    @Test
    public void testLoadWithHistoryFieldModel() throws Exception {
        //Act
        List<ReflectedField> fields = invokeLoadWithAssert(WithHistoryFieldModel.class, 2);

        //Assert
        ReflectedField otherVarField = fields.get(0);
        assertEquals("otherVar", otherVarField.getFieldName());
        assertFalse(otherVarField.keepsHistory());

        ReflectedField randomVarField = fields.get(1);
        assertEquals("randomVar", randomVarField.getFieldName());
        assertTrue(randomVarField.keepsHistory());
    }

    private List<ReflectedField> invokeLoadWithAssert(Class<?> clazz, int expectedFieldsSize) {
        //Act
        List<ReflectedField> fields = invokeLoadClassFieldsMethod(clazz);

        //Assert
        assertEquals(1, mirroredClassListMap.size());
        assertEquals(expectedFieldsSize, fields.size());

        return fields;
    }

    private List<ReflectedField> invokeLoadClassFieldsMethod(Class<?> clazz) {
        return ReflectionTestUtils.invokeMethod(service, "loadClassFields", clazz);
    }

    @Getter
    @Setter
    private class NotModifiableFieldModel {
        @Id
        private String id;
        @NotModifiable
        private String notModfiable;

        @OneToOne
        private String oneToOne;
        @OneToMany
        private String oneToMany;
        @ManyToOne
        private String manyToOne;
        @ManyToMany
        private String manyToMany;

        @OneToOne
        @ExternalModifiable
        private String externalModifiable;

        private String modifiable;
    }

    @Getter
    @Setter
    @ModifiableWithHistory
    private class WithHistoryModel {
        private String randomVar;
    }

    @Getter
    @Setter
    private class WithHistoryFieldModel {
        private String otherVar;
        @ModifiableWithHistory
        private String randomVar;
    }

}