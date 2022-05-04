package com.halo.dictionary.periodic;

import com.halo.dictionary.repository.PreferencesHelper;
import com.halo.dictionary.repository.impl.PreferenceStorage;
import com.halo.dictionary.repository.impl.PreferencesHelperImpl;

import org.jetbrains.annotations.NotNull;
import org.junit.Test;

import java.time.Duration;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.OptionalInt;

import static com.halo.dictionary.periodic.PeriodicWorkUtils.BLOCK_SIZE;
import static com.halo.dictionary.periodic.PeriodicWorkUtils.THRESHOLD_FOR_BLOCK_SIZE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class PeriodicWorkUtilsTest {

    @Test
    public void testComputeInitialDelay() {

        final LocalTime currentTimeBefore = LocalTime.of(8, 13);
        assertEquals(PeriodicWorkUtils.computeInitialDelay(currentTimeBefore), Duration.ofHours(3).plus(Duration.ofMinutes(47)));

        final LocalTime currentTimeAfter = LocalTime.of(12, 1);
        assertEquals(PeriodicWorkUtils.computeInitialDelay(currentTimeAfter), Duration.ofHours(23).plus(Duration.ofMinutes(59)));
    }

    private static class TestPreferenceStorage implements PreferenceStorage {
        private final Map<String, Integer> values = new HashMap<>();

        @Override
        public void saveInt(@NotNull String key, int value) {
            values.put(key, value);
        }

        @Override
        public int getInt(@NotNull String key, int defaultValue) {
            return values.getOrDefault(key, defaultValue);
        }
    }

    ;

    private final PreferencesHelper testPrefHelper = new PreferencesHelperImpl(new TestPreferenceStorage());

    @Test
    public void testGetNextRandomIndex() {

        assertEquals(OptionalInt.empty(), PeriodicWorkUtils.getNextRandomIndex(0, this.testPrefHelper));

        final List<Integer> overallAmountCases = Arrays.asList(
//                Math.max(THRESHOLD_FOR_BLOCK_SIZE - 1, 1),
//                THRESHOLD_FOR_BLOCK_SIZE + 1,
                THRESHOLD_FOR_BLOCK_SIZE * 2);

        overallAmountCases.forEach(overallAmount -> {
            testPrefHelper.dropBlockNumber();
            checkBlockSizeIteration(overallAmount);
        });
    }

    private void checkBlockSizeIteration(final int overallAmount) {

        final int actualBlockSize = overallAmount < THRESHOLD_FOR_BLOCK_SIZE ? BLOCK_SIZE / 2 : BLOCK_SIZE;
        int blocksAmount = overallAmount / actualBlockSize;
        if (overallAmount % actualBlockSize != 0) {
            blocksAmount++;
        }

        System.out.println("Start check: overallAmount=" + overallAmount
                + ", actualBlockSize=" + actualBlockSize
                + "; blocksAmount=" + blocksAmount);

        for (int i = 0; i < blocksAmount; i++) {

            final OptionalInt nextIndex = PeriodicWorkUtils.getNextRandomIndex(overallAmount, this.testPrefHelper);

            System.out.println(nextIndex);

            assertTrue(nextIndex.isPresent());
            if (i == blocksAmount - 1) {
                assertTrue(nextIndex.getAsInt() < actualBlockSize);
            } else {
                assertTrue(nextIndex.getAsInt() <= overallAmount);
                assertTrue(nextIndex.getAsInt() >= actualBlockSize * i);
            }
        }
    }

}