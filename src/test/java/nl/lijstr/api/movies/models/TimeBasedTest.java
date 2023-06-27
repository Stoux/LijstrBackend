package nl.lijstr.api.movies.models;

import lombok.Getter;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by Stoux on 5-2-2017.
 */
public class TimeBasedTest {

    private static int[] MINUTES = {0, -5, 5, 10, 22, -20};

    private List<TimeBased> originalList;
    private List<TimeBased> sortableList;

    @Before
    public void setUp() throws Exception {
        List<TimeBased> list = new ArrayList<>();
        for (int minute : MINUTES) {
            list.add(new TimeObj(minute));
        }
        originalList = Collections.unmodifiableList(list);
        sortableList = new ArrayList<>(list);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void dontModify() throws Exception {
        Collections.sort(originalList);
    }

    @Test
    public void createdOrder() throws Exception {
        //Act
        Collections.sort(sortableList);

        //Assert
        assertSpots(4, 0);
        assertSpots(3, 1);
        assertSpots(2, 2);
        assertSpots(0, 3);
        assertSpots(1, 4);
        assertSpots(5, 5);
    }

    @Test
    public void lastModifiedOrder() throws Exception {
        //Act
        sortableList.sort(TimeBased.lastModifiedComparator());

        //Assert (Should be exact reverse of other one as the constructor -'s the minute value)
        //See TimeObj#constructor
        assertSpots(4, 5);
        assertSpots(3, 4);
        assertSpots(2, 3);
        assertSpots(0, 2);
        assertSpots(1, 1);
        assertSpots(5, 0);
    }

    private void assertSpots(int originalSpot, int expectedNewSpot) {
        TimeBased timeBased = originalList.get(originalSpot);
        assertEquals(timeBased, sortableList.get(expectedNewSpot));
    }


    @Getter
    private class TimeObj implements TimeBased {
        private int minute;
        private LocalDateTime created;
        private LocalDateTime lastModified;

        private TimeObj(int minute) {
            this.minute = minute;
            created = LocalDateTime.now().plusMinutes(minute);
            lastModified = LocalDateTime.now().minusMinutes(minute);
        }

        @Override
        public String toString() {
            return "Minute: " + minute + " (" + (-minute) + ")";
        }
    }

}