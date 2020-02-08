package com.halo.dictionary.periodic;

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

import java.time.LocalTime;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

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

    public WordOfTheDayPeriodicWork(@NonNull final Context context, @NonNull final WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        final DictionaryRepository repository = DictionaryRepositoryFactory.createDictionaryRepository(getApplicationContext());
        final DictionaryRepository.Navigator navigator = repository.createNavigator();

        if (navigator.getEntriesAmount() > 0 && !PeriodicWorkUtils.userMightBeSleeping(LocalTime.now())) {
            final Optional<WordEntry> entryOpt = navigator.getEntryByIndex(
                    ThreadLocalRandom.current().nextInt(1, navigator.getEntriesAmount() + 1));
            entryOpt.ifPresent(entry -> sendWordNotification(entry.getWord(), entry.getTranslation()));
        }

        return Result.success();
    }

    private void sendWordNotification(@NonNull final String word, @NonNull final String translation) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            final NotificationManager manager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
            if (manager != null && manager.getNotificationChannel(NTF_CHANNEL) == null) {
                final NotificationChannel channel = new NotificationChannel(NTF_CHANNEL, NTF_CHANNEL, NotificationManager.IMPORTANCE_DEFAULT);
                channel.setDescription("Channel to display the word of the day");
                manager.createNotificationChannel(channel);
            }
        }

        final PendingIntent intent = PendingIntent.getActivity(
                getApplicationContext(),
                1,
                new Intent(getApplicationContext(), MainActivity.class),
                PendingIntent.FLAG_UPDATE_CURRENT);

        final NotificationCompat.Builder ntfBuilder = new NotificationCompat.Builder(getApplicationContext(), NTF_CHANNEL)
                .setContentTitle("The word of the day is " + word.toUpperCase() + "!")
                .setContentText("which means '" + translation + "'")
                .setContentIntent(intent)
                .setSmallIcon(R.drawable.button_add) // TODO icon
                .setAutoCancel(true);

        final NotificationManagerCompat manager = NotificationManagerCompat.from(getApplicationContext());
        manager.notify(NTF_ID, ntfBuilder.build());
    }
}
