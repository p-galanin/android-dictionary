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
        final PeriodicWorkRequest request = new PeriodicWorkRequest.Builder(WordOfTheDayPeriodicWork.class, 15, TimeUnit.MINUTES)
                .setInitialDelay(computeInitialDelay(LocalTime.now()))
                .build();
        final WorkManager manager = WorkManager.getInstance(viewForContext.getContext());
        manager.enqueueUniquePeriodicWork(WORD_OF_THE_DAY_WORK_NAME, ExistingPeriodicWorkPolicy.KEEP, request);
    }

    static Duration computeInitialDelay(@NonNull final LocalTime currentTime) {
        final LocalTime baseTime = LocalTime.of(NOTIFICATION_HOUR, 0, 0);
        final Duration resultDuration;

        if (currentTime.isAfter(baseTime)) {
            resultDuration = Duration.ofHours(24).minus(Duration.between(LocalTime.of(NOTIFICATION_HOUR, 0), currentTime));
        } else {
            resultDuration = Duration.between(currentTime, baseTime);
        }

        return resultDuration;
    }

}
