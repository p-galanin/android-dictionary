package com.halo.dictionary.periodic;

import com.halo.dictionary.repository.PreferencesHelper;

import org.junit.Test;

import java.time.Duration;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
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

    private final PreferencesHelper testPrefHelper = new PreferencesHelper() {

        int nextBlockNumber;

        @Override
        public int updateBlockNumber(final int blocksAmount) {
            final int currentNumber = this.nextBlockNumber;
            this.nextBlockNumber = currentNumber == blocksAmount - 1 ? 0 : currentNumber + 1;
            return currentNumber;
        }

        @Override
        public void dropBlockNumber() {
            this.nextBlockNumber = 0;
        }
    };

    @Test
    public void testGetNextRandomIndex() {

        assertEquals(OptionalInt.empty(), PeriodicWorkUtils.getNextRandomIndex(0, this.testPrefHelper));

        final List<Integer> overallAmountCases = Arrays.asList(
                Math.max(THRESHOLD_FOR_BLOCK_SIZE - 1, 1),
                THRESHOLD_FOR_BLOCK_SIZE + 1,
                THRESHOLD_FOR_BLOCK_SIZE * 2);

        overallAmountCases.forEach(overallAmount -> {
            testPrefHelper.dropBlockNumber();
            checkBlockSizeIteration(overallAmount);
        });
    }

    private void checkBlockSizeIteration(final int overallAmount) {

        final int actualBlockSize = overallAmount < THRESHOLD_FOR_BLOCK_SIZE ? BLOCK_SIZE / 2 : BLOCK_SIZE;
        final int blocksAmount = overallAmount / actualBlockSize;

        for (int i = 0; i < blocksAmount; i++) {

            final OptionalInt nextIndex = PeriodicWorkUtils.getNextRandomIndex(overallAmount, this.testPrefHelper);

            System.out.println(nextIndex);

            assertTrue(nextIndex.isPresent());
            assertTrue(nextIndex.getAsInt() >= actualBlockSize * i);

            if (i == blocksAmount - 1) {
                assertTrue(nextIndex.getAsInt() >= actualBlockSize * i);
            } else {
                assertTrue(nextIndex.getAsInt() < overallAmount);
            }

        }

        // Check returning to the first block (with index 0)
        final OptionalInt overflowIndex = PeriodicWorkUtils.getNextRandomIndex(overallAmount, this.testPrefHelper);
        assertTrue(overflowIndex.isPresent());
        assertTrue(overflowIndex.getAsInt() >= 0);
        assertTrue(overflowIndex.getAsInt() < actualBlockSize);
    }

}