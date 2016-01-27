package nl.lijstr.common;

import java.util.Arrays;
import java.util.List;
import org.junit.Test;

import static nl.lijstr.TestUtils.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Created by Stoux on 27/01/2016.
 */
public class StrUtilsTest {

    @Test
    public void testCollectionToDelimitedString() throws Exception {
        //Arrange
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);

        //Act
        String combined = StrUtils.collectionToDelimitedString(numbers, String::valueOf, "-");

        //Assert
        assertEquals("1-2-3-4-5", combined);
    }

}