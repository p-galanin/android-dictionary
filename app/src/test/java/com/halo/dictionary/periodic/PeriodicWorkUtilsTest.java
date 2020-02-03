package com.halo.dictionary.periodic;

import org.junit.Test;

import java.time.Duration;
import java.time.LocalTime;

import static org.junit.Assert.*;

public class PeriodicWorkUtilsTest {

    @Test
    public void testComputeInitialDelay() {

        final LocalTime currentTimeBefore = LocalTime.of(8, 13);
        assertEquals(PeriodicWorkUtils.computeInitialDelay(currentTimeBefore), Duration.ofHours(3).plus(Duration.ofMinutes(47)));

        final LocalTime currentTimeAfter = LocalTime.of(12, 1);
        assertEquals(PeriodicWorkUtils.computeInitialDelay(currentTimeAfter), Duration.ofHours(23).plus(Duration.ofMinutes(59)));
    }

}