package nl.lijstr.services.modify;

import nl.lijstr.common.ReflectUtils;
import nl.lijstr.domain.base.IdModel;
import nl.lijstr.domain.other.FieldHistory;
import nl.lijstr.repositories.abs.BasicRepository;
import nl.lijstr.repositories.other.FieldHistoryRepository;
import nl.lijstr.services.modify.models.ReflectedField;
import org.apache.logging.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static nl.lijstr._TestUtils.TestUtils.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Created by Stoux on 27/01/2016.
 */
public class FieldModifierServiceTest {

    private FieldModifierService service;
    private FieldHistoryRepository mockedHistoryRepo;

    private BasicRepository<FieldModel> basicRepository;
    private List<ReflectedField> reflectedFieldList;

    private Logger mockedLogger;

    @Before
    public void setUp() throws Exception {
        mockedHistoryRepo = mock(FieldHistoryRepository.class);

        service = new FieldModifierService();
        mockedLogger = mockLogger(service);
        insertMocks(service, mockedHistoryRepo);

        basicRepository = mock(BasicRepository.class);
        reflectedFieldList = reflectModelFields();

        when(basicRepository.saveAndFlush(any())).thenAnswer(invocation -> getInvocationParam(invocation, 0));
    }

    @Test
    public void testModify() throws Exception {
        //Arrange
        FieldModel original = new FieldModel("A", "A", null, null, "A");
        ReflectionTestUtils.setField(original, "id", 1L);
        FieldModel modifyWith = new FieldModel("A", "B", null, "A", null);

        //Act
        Optional<FieldModel> modifyResult = service.modify(basicRepository, original, modifyWith, reflectedFieldList);

        //Assert
        assertTrue(modifyResult.isPresent());
        assertEquals(original, modifyResult.get());

        assertEquals("A", original.var1);
        assertEquals("B", original.var2);
        assertNull(original.var3);
        assertEquals("A", original.varh4);
        assertNull(original.varh5);

        verify(basicRepository, times(1)).saveAndFlush(original);
        verify(mockedHistoryRepo, times(2)).save(any(FieldHistory.class));
    }

    @Test
    public void testNoChange() throws Exception {
        //Arrange
        FieldModel original = new FieldModel();

        //Act
        Optional<FieldModel> modifyResult = service.modify(basicRepository, original, original, reflectedFieldList);

        //Assert
        assertFalse(modifyResult.isPresent());
        verify(basicRepository, times(0)).saveAndFlush(any());
    }

    @Test
    public void testReflectiveException() throws Exception {
        //Arrange
        FieldModel original = new FieldModel();
        FieldModel modifyWith = new FieldModel("A", null, null, null, null);

        //Ghetto way to force a IllegalAccessException
        Method illegalMethod = FieldModifierServiceTest.class.getDeclaredMethod("illegalMethod");
        reflectedFieldList.get(0).setGetterMethod(illegalMethod);

        //Act
        Optional<FieldModel> modify = service.modify(basicRepository, original, modifyWith, reflectedFieldList);

        //Assert
        assertFalse(modify.isPresent());
        verify(mockedLogger, times(1)).warn(anyString(), anyString(), anyString(), any());
    }

    private String illegalMethod() {
        return "";
    }

    private List<ReflectedField> reflectModelFields() throws Exception {
        List<ReflectedField> list = new ArrayList<>();

        for (Field field : FieldModel.class.getDeclaredFields()) {
            if (!field.getName().startsWith("var")) {
                continue;
            }

            ReflectedField reflectedField = new ReflectedField(field);
            reflectedField.setKeepHistory(field.getName().startsWith("varh"));

            ReflectUtils.findFieldMethods(
                    FieldModel.class,
                    field.getName(),
                    reflectedField::setGetterMethod,
                    reflectedField::setSetterMethod
            );

            list.add(reflectedField);
        }

        return list;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public class FieldModel extends IdModel {
        private String var1;
        private String var2;
        private String var3;
        private String varh4;
        private String varh5;
    }


}