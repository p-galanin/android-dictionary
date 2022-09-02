package com.halo.dictionary.periodic;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.util.Log;

import com.halo.dictionary.mvp.WordEntry;
import com.halo.dictionary.repository.DictionaryRepository;
import com.halo.dictionary.repository.PreferencesHelper;

import java.time.LocalTime;
import java.util.Arrays;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Random;

import javax.inject.Named;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.hilt.work.HiltWorker;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import dagger.assisted.Assisted;
import dagger.assisted.AssistedInject;

import static com.halo.dictionary.periodic.NotificationUtilsKt.NTF_CHANNEL;


/**
 * Periodic notification with "the word of the day".
 */
@HiltWorker
public class WordOfTheDayPeriodicWork extends Worker {

    private static final String TAG = "WordOfTheDayPeriodicWork";
    private static final int NTF_ID = 100;
    private static final int AMOUNT_OF_NTF = 1;

    private final DictionaryRepository.Navigator navigator;
    private final NotificationManager notificationManager;
    private final PreferencesHelper preferencesHelper;

    @AssistedInject
    public WordOfTheDayPeriodicWork(@Assisted @NonNull final Context context,
                                    @Assisted @NonNull final WorkerParameters workerParams,
                                    @NonNull @Named("not archived") DictionaryRepository.Navigator navigator,
                                    @NonNull NotificationManager notificationManager,
                                    @NonNull PreferencesHelper preferencesHelper
    ) {
        super(context, workerParams);
        this.navigator = navigator;
        this.notificationManager = notificationManager;
        this.preferencesHelper = preferencesHelper;
    }

    @NonNull
    @Override
    public Result doWork() {
        if (this.navigator.getEntriesAmount() > 0 && !PeriodicWorkUtils.userMightBeSleeping(LocalTime.now())) {
            for (int i = 0; i < AMOUNT_OF_NTF; i++) {
                final OptionalInt randomIndex = PeriodicWorkUtils.getNextRandomIndex(
                        this.navigator.getEntriesAmount(),
                        this.preferencesHelper
                );
                if (randomIndex.isPresent()) {
                    final Optional<WordEntry> entryOpt = this.navigator.getEntryByIndex(randomIndex.getAsInt());
                    final int notificationOrderNumber = i;
                    if (!entryOpt.isPresent()) {
                        Log.e(TAG, "Entry for index " + randomIndex.getAsInt() + " not found");
                    }
                    entryOpt.ifPresent(entry -> sendWordNotification(entry, notificationOrderNumber));
                }
            }
        }

        return Result.success();
    }

    private void sendWordNotification(@NonNull final WordEntry wordEntry,
                                      final int notificationOrderNumber) {

        if (this.notificationManager.getNotificationChannel(NTF_CHANNEL) == null) {
            final NotificationChannel channel = new NotificationChannel(
                    NTF_CHANNEL, NTF_CHANNEL, NotificationManager.IMPORTANCE_DEFAULT
            );
            channel.setDescription("Channel to display the word to remember notifications");
            this.notificationManager.createNotificationChannel(channel);
        }

        int notificationId = NTF_ID + notificationOrderNumber;

        Notification notification = buildNotification(wordEntry, notificationId);
        if (notification == null) {
            Log.w(TAG, "Notification won't be send");
            return;
        }

        this.notificationManager.notify(notificationId, notification);
    }

    @Nullable
    private Notification buildNotification(final WordEntry wordEntry, int notificationId) {
        final Long id = wordEntry.getId();
        if (id == null) {
            Log.w(TAG, "Entry id is null");
            return null;
        }
        final Context context = getApplicationContext();
        final boolean isReverseTranslateDirection = new Random().nextBoolean();
        final String word = isReverseTranslateDirection ? wordEntry.getTranslation() : wordEntry.getWord();

        return NotificationUtilsKt.buildNotification(
                context,
                "Do you know what is it?",
                word,
                notificationId,
                Arrays.asList(new Known(context, id), new Show(context, wordEntry, isReverseTranslateDirection))
        );
    }
}
