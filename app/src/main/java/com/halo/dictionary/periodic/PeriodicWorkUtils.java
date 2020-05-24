package com.halo.dictionary.periodic;

import com.halo.dictionary.mvp.base.ViewBase;
import com.halo.dictionary.repository.PreferencesHelper;

import java.time.Duration;
import java.time.LocalTime;
import java.util.OptionalInt;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

public class PeriodicWorkUtils {

    private static final String WORD_OF_THE_DAY_WORK_NAME = "WordOfTheDay";
    private static int NOTIFICATION_HOUR = 12;

    static final int BLOCK_SIZE = 10;
    static final int THRESHOLD_FOR_BLOCK_SIZE = 50;

    private static final Random randomGenerator = new Random();

    /**
     * Starts (if not started) the periodic notification with the word of the day.
     *
     * @param viewForContext view to get application context, not null
     */
    public static void startWordOfTheDayNotifications(@NonNull final ViewBase viewForContext) {
        // final PeriodicWorkRequest request = new PeriodicWorkRequest.Builder(WordOfTheDayPeriodicWork.class, 1, TimeUnit.HOURS) // TODO restore after debug
        final PeriodicWorkRequest request = new PeriodicWorkRequest.Builder(WordOfTheDayPeriodicWork.class, 15, TimeUnit.MINUTES)
                // .setInitialDelay(computeInitialDelay(LocalTime.now())) // TODO remove after debug
                .build();
        final WorkManager manager = WorkManager.getInstance(viewForContext.getViewContext());
        manager.enqueueUniquePeriodicWork(WORD_OF_THE_DAY_WORK_NAME, ExistingPeriodicWorkPolicy.KEEP, request);
    }

    public static void stopWordOfTheDayNotifications(@NonNull final ViewBase viewForContext) {
        final WorkManager manager = WorkManager.getInstance(viewForContext.getViewContext());
        manager.cancelUniqueWork(WORD_OF_THE_DAY_WORK_NAME);
    }

    static Duration computeInitialDelay(@NonNull final LocalTime currentTime) {
        final LocalTime baseTime = LocalTime.of(NOTIFICATION_HOUR, 0, 0);
        final Duration resultDuration;

        if (currentTime.isAfter(baseTime)) {
            resultDuration = Duration.ofHours(24).minus(Duration.between(baseTime, currentTime));
        } else {
            resultDuration = Duration.between(currentTime, baseTime);
        }

        return resultDuration;
    }

    /**
     * Determines whether it too early or too late to bother the user.
     * @param currentTime specified time, not null
     * @return {@code true} if it's too early or too late to bother the user, {@code false} otherwise
     */
    static boolean userMightBeSleeping(@NonNull final LocalTime currentTime) {
        return currentTime.getHour() < 12 || currentTime.getHour() > 21;
    }

    static OptionalInt getNextRandomIndex(final int overallAmount, final PreferencesHelper preferencesHelper) {

        if (overallAmount == 0) {
            return OptionalInt.empty();
        }

        int blockSize = computeBlockSize(overallAmount);

        if (overallAmount < blockSize * 2) { // two blocks min
            return OptionalInt.of(randomGenerator.nextInt(overallAmount));
        }

        int blocksAmount = overallAmount / blockSize;

        final int currentBlockNumber = preferencesHelper.updateBlockNumber(blocksAmount);
        final int result = currentBlockNumber * blockSize + randomGenerator.nextInt(
                currentBlockNumber == blocksAmount - 1 ? (blockSize + overallAmount % blockSize) : blockSize);

        return OptionalInt.of(result);
    }

    private static int computeBlockSize(final int overallAmount) {
        return overallAmount < THRESHOLD_FOR_BLOCK_SIZE ? BLOCK_SIZE / 2 : BLOCK_SIZE;
    }


}
