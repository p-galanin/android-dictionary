package com.halo.dictionary.periodic

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.halo.dictionary.R
import com.halo.dictionary.mvp.WordEntry

sealed class NotificationAction {
    protected abstract val requestCode: Int

    abstract fun addToNotificationBuilder(
        notificationId: Int,
        builder: NotificationCompat.Builder,
        context: Context
    )

    protected fun createPendingIntent(intent: Intent, context: Context): PendingIntent =
        PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
}

data class Known(val context: Context, val wordId: Long) : NotificationAction() {
    override val requestCode = 1

    override fun addToNotificationBuilder(
        notificationId: Int,
        builder: NotificationCompat.Builder,
        context: Context
    ) {
        val intent = NotificationActionReceiver.createIntent(
            context,
            ACTION.KNOWN,
            notificationId,
            wordId,
        )
        builder.addAction(
            R.drawable.button_add,
            "Known",
            createPendingIntent(intent, context),
        )
    }
}

data class Show(
    val context: Context,
    val entry: WordEntry,
    val isReverseTranslation: Boolean,
) : NotificationAction() {
    override val requestCode = 2

    override fun addToNotificationBuilder(
        notificationId: Int,
        builder: NotificationCompat.Builder,
        context: Context
    ) {
        val intent = NotificationActionReceiver.createIntent(
            context,
            ACTION.SHOW,
            notificationId,
            entry.id,
            "'${if (isReverseTranslation) entry.translation else entry.word}' is",
            if (isReverseTranslation) entry.word else entry.translation
        )
        builder.addAction(
            R.drawable.button_add,
            "Show",
            createPendingIntent(intent, context),
        )
    }
}

data class Ok(val context: Context) : NotificationAction() {
    override val requestCode = 3

    override fun addToNotificationBuilder(
        notificationId: Int,
        builder: NotificationCompat.Builder,
        context: Context
    ) {
        val intent = NotificationActionReceiver.createIntent(
            context,
            ACTION.OK,
            notificationId
        )
        builder.addAction(
            R.drawable.button_add,
            "OK",
            createPendingIntent(intent, context),
        )
    }
}

