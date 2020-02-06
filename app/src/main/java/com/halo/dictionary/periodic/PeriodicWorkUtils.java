package com.halo.dictionary.periodic;

import com.halo.dictionary.mvp.base.ViewBase;

import java.time.Duration;
import java.time.LocalTime;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

public class PeriodicWorkUtils {

    private static final String WORD_OF_THE_DAY_WORK_NAME = "WordOfTheDay";
    private static int NOTIFICATION_HOUR = 12;

    /**
     * Starts (if not started) the periodic notification with the word of the day.
     *
     * @param viewForContext view to get application context, not null
     */
    public static void startWordOfTheDayNotifications(@NonNull final ViewBase viewForContext) {
        final PeriodicWorkRequest request = new PeriodicWorkRequest.Builder(WordOfTheDayPeriodicWork.class, 1, TimeUnit.HOURS)
                .setInitialDelay(computeInitialDelay(LocalTime.now()))
                .build();
        final WorkManager manager = WorkManager.getInstance(viewForContext.getContext());
        manager.enqueueUniquePeriodicWork(WORD_OF_THE_DAY_WORK_NAME, ExistingPeriodicWorkPolicy.KEEP, request);
    }

    public static void stopWordOfTheDayNotifications(@NonNull final ViewBase viewForContext) {
        final WorkManager manager = WorkManager.getInstance(viewForContext.getContext());
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

}
