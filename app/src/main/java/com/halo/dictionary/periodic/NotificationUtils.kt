package com.halo.dictionary.periodic

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.halo.dictionary.R
import com.halo.dictionary.mvp.ui.MainActivity

internal const val NTF_CHANNEL = "THE_WORD_OF_THE_DAY"

internal fun buildNotification(
    context: Context,
    title: String,
    text: String,
    notificationId: Int,
    withActions: Boolean,
): Notification {
    val builder = NotificationCompat.Builder(context, NTF_CHANNEL)
        .setContentTitle(title)
        .setContentText(text)
        .setContentIntent(createOpenWordsIntent(context))
        .setSmallIcon(R.drawable.button_add) // TODO icon
        .extend(
            NotificationCompat.WearableExtender()
                .addPage(createWearSecondPage(context, text))
        )
        .setAutoCancel(true)

    // todo pg refactor: deduplicate ntf sending code
    if (withActions) {
        builder.addAction(
            R.drawable.button_add,
            "Known",
            createHideNotificationIntent(context, notificationId)
        )
        builder.addAction(
            R.drawable.button_add,
            "Show",
            creteShowTranslationEvent(context, notificationId)
        )
    }

    return builder.build()
}

private fun createOpenWordsIntent(context: Context): PendingIntent? {
    return PendingIntent.getActivity(
        context,
        1,
        Intent(context, MainActivity::class.java),
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )
}

private fun createWearSecondPage(
    context: Context,
    text: String,
): Notification {
    return NotificationCompat.Builder(context, NTF_CHANNEL)
        .setStyle(NotificationCompat.BigTextStyle())
        .setContentText(text)
        .setAutoCancel(true)
        .build()
}

private fun createHideNotificationIntent(context: Context, notificationId: Int): PendingIntent {
    return PendingIntent.getBroadcast(
        context,
        2,
        Intent(context, NotificationActionReceiver::class.java),
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )
}

private fun creteShowTranslationEvent(context: Context, notificationId: Int): PendingIntent {
    return PendingIntent.getBroadcast(
        context,
        3,
        Intent(context, NotificationActionReceiver::class.java),
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )
}