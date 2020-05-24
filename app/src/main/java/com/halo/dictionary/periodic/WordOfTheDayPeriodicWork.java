package com.halo.dictionary.periodic;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.halo.dictionary.R;
import com.halo.dictionary.mvp.WordEntry;
import com.halo.dictionary.mvp.ui.MainActivity;
import com.halo.dictionary.repository.DictionaryRepository;
import com.halo.dictionary.repository.DictionaryRepositoryFactory;
import com.halo.dictionary.repository.impl.PreferencesHelperImpl;

import java.util.Optional;
import java.util.OptionalInt;
import java.util.Random;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;


/**
 * Periodic notification with "the word of the day".
 */
public class WordOfTheDayPeriodicWork extends Worker {

    private static final String NTF_CHANNEL = "THE_WORD_OF_THE_DAY";
    private static final int NTF_ID = 100;
    private static final int AMOUNT_OF_NTF = 2;

    public WordOfTheDayPeriodicWork(@NonNull final Context context, @NonNull final WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        final DictionaryRepository repository = DictionaryRepositoryFactory.createDictionaryRepository(getApplicationContext());
        final DictionaryRepository.Navigator navigator = repository.createNavigator();

//        if (navigator.getEntriesAmount() > 0 && !PeriodicWorkUtils.userMightBeSleeping(LocalTime.now())) { TODO return after debug
        if (navigator.getEntriesAmount() > 0) {
            for (int i = 0; i < AMOUNT_OF_NTF; i++) {
                final OptionalInt randomIndex = PeriodicWorkUtils.getNextRandomIndex(
                        navigator.getEntriesAmount(), new PreferencesHelperImpl(getApplicationContext()));
                if (randomIndex.isPresent()) {
                    final Optional<WordEntry> entryOpt = navigator.getEntryByIndex(randomIndex.getAsInt());
                    final int notificationOrderNumber = i;
                    entryOpt.ifPresent(entry -> sendWordNotification(entry, notificationOrderNumber));
                }
            }
        }

        return Result.success();
    }

    private void sendWordNotification(@NonNull final WordEntry wordEntry, final int notificationOrderNumber) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            final NotificationManager manager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
            if (manager != null && manager.getNotificationChannel(NTF_CHANNEL) == null) {
                final NotificationChannel channel = new NotificationChannel(NTF_CHANNEL, NTF_CHANNEL, NotificationManager.IMPORTANCE_DEFAULT);
                channel.setDescription("Channel to display the word of the day");
                manager.createNotificationChannel(channel);
            }
        }

        final NotificationManagerCompat manager = NotificationManagerCompat.from(getApplicationContext());
        manager.notify(NTF_ID + notificationOrderNumber, buildNotification(wordEntry));
    }

    private Notification buildNotification(final WordEntry wordEntry) {
        final boolean isReverseTranslateDirection = new Random().nextBoolean();
        final String title = isReverseTranslateDirection ? wordEntry.getTranslation() : wordEntry.getWord();
        final String text = isReverseTranslateDirection ? wordEntry.getWord() : wordEntry.getTranslation();
        return new NotificationCompat.Builder(getApplicationContext(), NTF_CHANNEL)
                .setContentTitle(title.toUpperCase())
                .setStyle(new NotificationCompat.BigTextStyle()
                        .setBigContentTitle(text)
                        .bigText("\n\n\n"))
                .setContentIntent(createIntent())
                .setSmallIcon(R.drawable.button_add) // TODO icon
                .extend(new NotificationCompat.WearableExtender()
                        .addPage(createWearSecondPage(wordEntry, isReverseTranslateDirection))
                )
                .setAutoCancel(true)
                .build();
    }

    private PendingIntent createIntent() {
        return PendingIntent.getActivity(
                getApplicationContext(),
                1,
                new Intent(getApplicationContext(), MainActivity.class),
                PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private Notification createWearSecondPage(final WordEntry wordEntry, final boolean isReverseTranslationDirection) {
        return new NotificationCompat.Builder(getApplicationContext(), NTF_CHANNEL)
                .setContentTitle(isReverseTranslationDirection ? wordEntry.getWord() : wordEntry.getTranslation())
                .setAutoCancel(true)
                .build();
    }
}
