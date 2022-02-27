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
    actions: List<NotificationAction>
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

    actions.forEach {
        it.addToNotificationBuilder(notificationId, builder, context)
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