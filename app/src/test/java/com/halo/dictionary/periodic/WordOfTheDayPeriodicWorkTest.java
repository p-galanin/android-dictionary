package com.halo.dictionary.periodic;

import android.content.Context;

import org.junit.Test;
import org.mockito.Mockito;

import java.time.LocalTime;

import androidx.work.WorkManager;
import androidx.work.WorkerParameters;

import static org.junit.Assert.*;

public class WordOfTheDayPeriodicWorkTest {

    @Test
    public void testIsUserMightSleeping() {
        assertTrue(PeriodicWorkUtils.userMightBeSleeping(LocalTime.of(11, 59)));
        assertTrue(PeriodicWorkUtils.userMightBeSleeping(LocalTime.of(22, 0)));
        assertFalse(PeriodicWorkUtils.userMightBeSleeping(LocalTime.of(21, 15)));
        assertFalse(PeriodicWorkUtils.userMightBeSleeping(LocalTime.of(12, 0)));
    }
}