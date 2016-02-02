package nl.lijstr.services.maf.handlers.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.*;
import nl.lijstr.domain.base.IdModel;
import nl.lijstr.domain.other.FieldHistory;
import nl.lijstr.domain.other.FieldHistorySuggestion;
import nl.lijstr.exceptions.LijstrException;
import nl.lijstr.repositories.other.FieldHistoryRepository;
import nl.lijstr.repositories.other.FieldHistorySuggestionRepository;
import org.junit.Before;
import org.junit.Test;

import static nl.lijstr._TestUtils.TestUtils.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Created by Leon Stam on 29-1-2016.
 */
public class FieldModifyHandlerTest {

    private FieldHistoryRepository mockHistoryRepo;
    private FieldHistorySuggestionRepository mockSuggestionRepo;

    private FieldModifyHandler handler;

    @Before
    public void setUp() {
        handler = null;
        mockHistoryRepo = mock(FieldHistoryRepository.class);
        mockSuggestionRepo = mock(FieldHistorySuggestionRepository.class);
    }

    @Test
    public void testModify() throws Exception {
        //Arrange
        TestModify testModify = new TestModify("TEST", null, null, 10);
        OtherTestModify otherTestModify = new OtherTestModify(null, null, "TESTX", 10);
        handler = createHandlerWithoutHistory(testModify, otherTestModify);

        //Act
        handler.modify("var");
        handler.modify("nullVar");
        handler.modify("varEqual");

        //Act - 2 Params
        handler.modify("varX", "varY");

        //Assert
        assertNull(testModify.var);
        assertNull(testModify.nullVar);

        assertNotNull(testModify.varX);
        assertEquals(otherTestModify.varY, testModify.varX);
    }

    @Test(expected = LijstrException.class)
    public void testInvalidClass() throws Exception {
        //Arrange
        InvalidModel model = new InvalidModel();
        handler = createHandlerWithoutHistory(model, model);

        //Act
        handler.modify("var");

        //Assert
        fail();
    }

    @Test
    public void testCompareAndModify() throws Exception {
        //Arrange
        TestModify testModify = new TestModify("TEST", null, null, 10);
        DifferentModel differentModel = new DifferentModel(9001);
        handler = createHandlerWithoutHistory(testModify, differentModel);

        //Act
        handler.compareAndModify(
                "var", testModify.var, differentModel.varEqual,
                integer -> "x" + integer, testModify::setVar
        );

        //Assert
        assertEquals("x9001", testModify.getVar());
    }

    @Test
    public void testFieldHistory() throws Exception {
        //Arrange
        TestModify original = new TestModify("TEST", "X", null, 10);
        original.setId(1L);
        TestModify newValues = new TestModify("Real", "X", null, 5);

        String className = FieldHistory.getDatabaseClassName(TestModify.class);
        FieldHistory reapplyAttempt = new FieldHistory(className, 1L, "nullVar", "Real", "TEST");
        FieldHistory newDefault = new FieldHistory(className, 1L, "varX", "Y", "X");
        FieldHistory newSuggestion = new FieldHistory(className, 1L, "varEqual", "7", "10");

        when(mockHistoryRepo.findByObjectIdAndClassName(anyLong(), anyString()))
                .thenReturn(Arrays.asList(reapplyAttempt, newDefault, newSuggestion));
        final List<FieldHistorySuggestion> madeSuggestions = new ArrayList<>();
        when(mockSuggestionRepo.saveAndFlush(any()))
                .thenAnswer(invocation -> {
                    FieldHistorySuggestion suggestion = getInvocationParam(invocation, 0);
                    madeSuggestions.add(suggestion);
                    return suggestion;
                });


        handler = new FieldModifyHandler(original, newValues, mockHistoryRepo, mockSuggestionRepo);

        //Execute
        handler.modify("nullVar");
        handler.modify("varX");
        handler.modify("varEqual");

        //Assert
        assertEquals("TEST", original.nullVar);

        verify(mockHistoryRepo, times(1)).delete(newDefault);

        assertEquals(1, madeSuggestions.size());
        assertEquals(newSuggestion, madeSuggestions.get(0).getOverwriteTarget());
        assertEquals("5", madeSuggestions.get(0).getSuggestedChange());
    }

    private FieldModifyHandler createHandlerWithoutHistory(IdModel original, Object newObject) {
        when(mockHistoryRepo.findByObjectIdAndClassName(anyLong(), anyString()))
                .thenReturn(new ArrayList<>());

        original.setId(1L);
        return new FieldModifyHandler(original, newObject, mockHistoryRepo, mockSuggestionRepo);
    }

    @Setter
    @Getter
    @AllArgsConstructor
    public class TestModify extends IdModel {
        String nullVar;
        String varX;
        private String var;
        int varEqual;
    }

    @Setter
    @Getter
    @AllArgsConstructor
    public class OtherTestModify {
        public String var;
        String nullVar;
        String varY;
        int varEqual;
    }

    public class InvalidModel extends IdModel {
        private String var;
    }

    @AllArgsConstructor
    public class DifferentModel {
        int varEqual;
    }

}