package com.halo.dictionary.periodic;

import org.junit.Test;

import java.time.LocalTime;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class WordOfTheDayPeriodicWorkTest {

    @Test
    public void testIsUserMightSleeping() {
        assertTrue(PeriodicWorkUtils.userMightBeSleeping(LocalTime.of(8, 59)));
        assertTrue(PeriodicWorkUtils.userMightBeSleeping(LocalTime.of(23, 0)));
        assertFalse(PeriodicWorkUtils.userMightBeSleeping(LocalTime.of(22, 15)));
        assertFalse(PeriodicWorkUtils.userMightBeSleeping(LocalTime.of(9, 0)));
    }
}