package com.halo.dictionary.periodic

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.halo.dictionary.R
import com.halo.dictionary.mvp.WordEntryKt

sealed class NotificationAction() {
    abstract fun addToNotificationBuilder(
        notificationId: Int,
        builder: NotificationCompat.Builder,
        context: Context
    )

    protected fun createPendingIntent(intent: Intent, context: Context): PendingIntent =
        PendingIntent.getBroadcast(
            context,
            REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

    companion object {
        private const val REQUEST_CODE = 1
    }
}

data class Known(val context: Context, val wordId: Long) : NotificationAction() {
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
    val entry: WordEntryKt,
    val isReverseTranslation: Boolean,
) : NotificationAction() {

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

