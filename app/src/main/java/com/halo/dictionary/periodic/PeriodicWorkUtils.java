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
    private static final int NOTIFICATION_HOUR = 12;
    private static final int NOTIFICATION_PERIOD_MINUTES = 60;

    static final int BLOCK_SIZE = 10;
    static final int THRESHOLD_FOR_BLOCK_SIZE = 50;

    private static final Random randomGenerator = new Random();

    /**
     * Starts (if not started) the periodic notification with the word of the day.
     */
    public static void startWordOfTheDayNotifications(@NonNull WorkManager workManger) {
        final PeriodicWorkRequest request = new PeriodicWorkRequest.Builder(
                WordOfTheDayPeriodicWork.class,
                NOTIFICATION_PERIOD_MINUTES, // todo pg user settings (reschedule after change)
                TimeUnit.MINUTES
        )
                // .setInitialDelay(computeInitialDelay(LocalTime.now())) // TODO remove after debug
                .build();
        workManger.enqueueUniquePeriodicWork(
                WORD_OF_THE_DAY_WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP, request
        );
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
        return currentTime.getHour() < 9 || currentTime.getHour() > 22;
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
        if (overallAmount % blockSize != 0) {
            blocksAmount++;
        }

        final int newBlockNumber = preferencesHelper.updateBlockNumber(blocksAmount);
        final int maxIndex = (newBlockNumber == blocksAmount - 1) ? (overallAmount % blockSize) : blockSize;
        int index;
        if (maxIndex == 0) {
            index = 0;
        } else {
            index = randomGenerator.nextInt(maxIndex);
        }
        int result = newBlockNumber * blockSize + index;
        return OptionalInt.of(result);
    }

    private static int computeBlockSize(final int overallAmount) {
        return overallAmount < THRESHOLD_FOR_BLOCK_SIZE ? BLOCK_SIZE / 2 : BLOCK_SIZE;
    }


}
