package nl.lijstr.services.maf.handlers.util;

import java.time.LocalDate;
import java.util.function.Function;
import nl.lijstr.services.maf.handlers.util.FieldConverters;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Leon Stam on 29-1-2016.
 */
public class FieldConvertersTest {

    private Function<String, Object> objectFunction;

    @Before
    public void setUp() throws Exception {
        objectFunction = null;
    }

    @Test
    public void testConvertToDate() throws Exception {
        //Arrange
        this.objectFunction = FieldConverters::convertToDate;

        //Act - Valid
        exec("2009", LocalDate.of(2009, 1, 1));
        exec("200903", LocalDate.of(2009, 3, 1));
        exec("20161217", LocalDate.of(2016, 12, 17));
        exec("20163", LocalDate.of(2016, 1, 1));

        //Act - Invalid
        execInvalid("wata");
        execInvalid("20163333");
    }

    @Test
    public void testConvertToYear() throws Exception {
        //Arrange
        this.objectFunction = FieldConverters::convertToYear;

        //Act - Valid
        exec("2012", 2012);
        exec("2020", 2020);
        exec("1932", 1932);

        //Act - Invalid
        execInvalid("AAAA");
        execInvalid("133");
        execInvalid("19999");
    }

    @Test
    public void testConvertToDouble() throws Exception {
        //Arrange
        this.objectFunction = FieldConverters::convertToDouble;

        //Act - Valid
        exec("12.0", 12.0);
        exec("12,0", 12.0);
        exec("12", 12.0);
        exec("333.999", 333.999);

        //Act - Invalid
        execInvalid("A");
        execInvalid("1,333.123");
        execInvalid("333.-12");
    }

    @Test
    public void testConvertToLong() throws Exception {
        //Arrange
        this.objectFunction = FieldConverters::convertToLong;

        //Act - Valid
        exec("12000", 12000L);
        exec("12.000", 12000L);
        exec("12,000", 12000L);

        //Act - Invalid
        execInvalid("-12000");
        execInvalid("1200xD");
    }

    @Test
    public void testConvertMetaCriticScore() throws Exception {
        //Arrange
        this.objectFunction = FieldConverters::convertMetaCriticScore;

        //Act - Valid
        exec("00/100", 0);
        exec("0/100", 0);
        exec("15/100", 15);
        exec("100/100", 100);

        //Act - Invalid
        execInvalid("-1/100");
        execInvalid("101/100");
        execInvalid("99/99");
        execInvalid("9999");
        execInvalid("99");
        execInvalid("AA/100");
    }

    @SuppressWarnings("unchecked")
    private <X> void exec(String inputString, X expected) {
        X result = (X) objectFunction.apply(inputString);
        assertEquals(expected, result);
    }

    private void execInvalid(String inputString) {
        try {
            Object result = objectFunction.apply(inputString);
            assertNull(result);
        } catch (Exception e) {
            //Expected
        }
    }

}