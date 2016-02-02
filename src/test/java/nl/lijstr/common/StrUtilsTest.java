package nl.lijstr.common;

import java.util.Arrays;
import java.util.List;
import nl.lijstr._TestUtils.TestUtils;
import org.junit.Test;

import static org.junit.Assert.*;

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

    @Test
    public void testUseOrDefault() {
        //Arrange
        String defaultReturn = "DEFAULT";
        String test1 = "a";
        String test2 = " ";
        String test3 = "";

        //Act
        String result1 = StrUtils.useOrDefault(test1, defaultReturn);
        String result2 = StrUtils.useOrDefault(test2, defaultReturn);
        String result3 = StrUtils.useOrDefault(test3, defaultReturn);
        String result4 = StrUtils.useOrDefault(null, defaultReturn);

        //Assert
        assertEquals(test1, result1);
        assertEquals(test2, result2);
        assertEquals(defaultReturn, result3);
        assertEquals(defaultReturn, result4);
    }

    @Test
    public void coverPrivateConstructor() throws Exception {
        TestUtils.callPrivateConstructor(StrUtils.class);
    }

    @Test
    public void testStingOrNull() throws Exception {
        //Arrange
        Integer i = 5;
        String empty = "";
        Object nObject = null;
        String s = "Hello";

        //Act
        String intResult = StrUtils.stringOrNull(i);
        String emptyResult = StrUtils.stringOrNull(empty);
        String nullResult = StrUtils.stringOrNull(nObject);
        String helloResult = StrUtils.stringOrNull(s);

        //Assert
        assertEquals("5", intResult);
        assertNull(emptyResult);
        assertNull(nullResult);
        assertEquals(s, helloResult);
    }

}