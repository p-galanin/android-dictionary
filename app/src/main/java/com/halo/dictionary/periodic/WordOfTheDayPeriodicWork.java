package com.halo.dictionary.periodic;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.halo.dictionary.R;
import com.halo.dictionary.mvp.WordEntryKt;
import com.halo.dictionary.mvp.ui.MainActivity;
import com.halo.dictionary.repository.DictionaryRepository;
import com.halo.dictionary.repository.DictionaryRepositoryFactory;
import com.halo.dictionary.repository.impl.PreferencesHelperImpl;

import java.time.LocalTime;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Random;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
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
                    final Optional<WordEntryKt> entryOpt = navigator.getEntryByIndex(randomIndex.getAsInt());
                    final int notificationOrderNumber = i;
                    entryOpt.ifPresent(entry -> sendWordNotification(entry, notificationOrderNumber));
                }
            }
        }

        return Result.success();
    }

    private void sendWordNotification(@NonNull final WordEntryKt wordEntry,
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

        manager.notify(notificationId, buildNotification(wordEntry, notificationId));
    }

    private Notification buildNotification(final WordEntryKt wordEntry, int notificationId) {
        final boolean isReverseTranslateDirection = new Random().nextBoolean();
        final String word = isReverseTranslateDirection ? wordEntry.getTranslation() : wordEntry.getWord();
//        final String text = isReverseTranslateDirection ? wordEntry.getWord() : wordEntry.getTranslation();
        return new NotificationCompat.Builder(getApplicationContext(), NTF_CHANNEL)
                .setContentText("What does it mean?")
                .setContentTitle(word)
                .setContentIntent(createIntent())
                .setSmallIcon(R.drawable.button_add) // TODO icon
                .extend(new NotificationCompat.WearableExtender()
                        .addPage(createWearSecondPage(wordEntry, isReverseTranslateDirection))
                )
                .setAutoCancel(true)
                .addAction(R.drawable.button_add, "Known", createHideNotificationIntent(notificationId, wordEntry))
                .addAction(R.drawable.button_add, "Show", creteShowTranslationEvent(notificationId, wordEntry, isReverseTranslateDirection))
                .build();
    }

    @NonNull
    private PendingIntent createHideNotificationIntent(int notificationId,
                                                       WordEntryKt wordEntry) {
        return PendingIntent.getBroadcast(getApplicationContext(),
                2,
                NotificationActionReceiver.createIntent(
                        getApplicationContext(),
                        ACTION.KNOWN,
                        notificationId,
                        wordEntry.getId()),
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
    }

    @NonNull
    private PendingIntent creteShowTranslationEvent(int notificationId,
                                                    WordEntryKt entry,
                                                    boolean isReverseTranslation) {
        return PendingIntent.getBroadcast(getApplicationContext(),
                3,
                NotificationActionReceiver.createIntent(
                        getApplicationContext(),
                        ACTION.SHOW,
                        notificationId,
                        entry.getId(),
                        "The translation is",
                        isReverseTranslation ? entry.getWord() : entry.getTranslation()),
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
    }


    private PendingIntent createIntent() {
        return PendingIntent.getActivity(
                getApplicationContext(),
                1,
                new Intent(getApplicationContext(), MainActivity.class),
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
    }

    private Notification createWearSecondPage(final WordEntryKt wordEntry,
                                              final boolean isReverseTranslationDirection) {
        return new NotificationCompat.Builder(getApplicationContext(), NTF_CHANNEL)
                .setStyle(new NotificationCompat.BigTextStyle())
                .setContentText(isReverseTranslationDirection ? wordEntry.getWord() : wordEntry.getTranslation())
                .setAutoCancel(true)
                .build();
    }
}
