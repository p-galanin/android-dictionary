package com.halo.dictionary.periodic;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.util.Log;

import com.halo.dictionary.mvp.WordEntry;
import com.halo.dictionary.repository.DictionaryRepository;
import com.halo.dictionary.repository.DictionaryRepositoryFactory;
import com.halo.dictionary.repository.impl.PreferencesHelperImpl;

import java.time.LocalTime;
import java.util.Arrays;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Random;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import static com.halo.dictionary.periodic.NotificationUtilsKt.NTF_CHANNEL;


/**
 * Periodic notification with "the word of the day".
 */
public class WordOfTheDayPeriodicWork extends Worker {

    private static final String TAG = "WordOfTheDayPeriodicWork";

    private static final int NTF_ID = 100;
    private static final int AMOUNT_OF_NTF = 1;

    public WordOfTheDayPeriodicWork(@NonNull final Context context,
                                    @NonNull final WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        final DictionaryRepository repository = DictionaryRepositoryFactory.createDictionaryRepository(getApplicationContext());
        final DictionaryRepository.Navigator navigator = repository.createNavigator(false);


        if (navigator.getEntriesAmount() > 0 && !PeriodicWorkUtils.userMightBeSleeping(LocalTime.now())) {
            for (int i = 0; i < AMOUNT_OF_NTF; i++) {
                final OptionalInt randomIndex = PeriodicWorkUtils.getNextRandomIndex(
                        navigator.getEntriesAmount(), new PreferencesHelperImpl(getApplicationContext()));
                if (randomIndex.isPresent()) {
                    final Optional<WordEntry> entryOpt = navigator.getEntryByIndex(randomIndex.getAsInt());
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

        final NotificationManager manager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        if (manager == null) {
            Log.w(TAG, "Notification won't be sent: notification manager is null");
            return;
        }

        if (manager.getNotificationChannel(NTF_CHANNEL) == null) {
            final NotificationChannel channel = new NotificationChannel(
                    NTF_CHANNEL, NTF_CHANNEL, NotificationManager.IMPORTANCE_DEFAULT
            );
            channel.setDescription("Channel to display the word to remember notifications");
            manager.createNotificationChannel(channel);
        }

        int notificationId = NTF_ID + notificationOrderNumber;

        Notification notification = buildNotification(wordEntry, notificationId);
        if (notification == null) {
            Log.w(TAG, "Notification won't be send");
            return;
        }

        manager.notify(notificationId, notification);
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
