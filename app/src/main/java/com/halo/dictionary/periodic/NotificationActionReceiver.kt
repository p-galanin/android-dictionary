package com.halo.dictionary.periodic

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.halo.dictionary.mvp.WordEntryKt
import com.halo.dictionary.repository.impl.sql.WordDbHelper

class NotificationActionReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        Log.i(TAG, "Broadcast received, extras: ${intent.extras}")
        val action = intent.extras?.getSerializable(ACTION_EXTRA)
        val notificationId = intent.extras?.getInt(NTF_ID_EXTRA)
        val wordId = intent.extras?.getLong(WORD_ID_EXTRA)

        if (action !is ACTION || notificationId == null || wordId == null) {
            Log.w(TAG, "Wrong intent extra")
            return
        }

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE)
            as? NotificationManager
            ?: return

        when (action) {
            ACTION.KNOWN -> {
                manager.cancel(notificationId)
                updateWordWeight(context, wordId)
            }
            ACTION.SHOW -> manager.notify(
                notificationId,
                buildNotification(
                    context,
                    intent.extras?.getString(TITLE_EXTRA) ?: "?",
                    intent.extras?.getString(TEXT_EXTRA) ?: "?",
                    notificationId,
                    listOf(Known(context, wordId), Ok(context))
                )
            )
            ACTION.OK -> {
                manager.cancel(notificationId)
            }
        }
    }

    private fun updateWordWeight(context: Context, wordId: Long) {
        WordDbHelper.getInstance(context).getEntryById(wordId).ifPresent {
            WordDbHelper.getInstance(context).update(
                WordEntryKt(it.word, it.translation, it.weight + 1, it.isArchived, it.id)
            )
        }
    }

    companion object {
        private const val TAG = "NotificationActionReceiver"

        private const val ACTION_EXTRA = "action_type"
        private const val NTF_ID_EXTRA = "notification_id"
        private const val WORD_ID_EXTRA = "word_id"
        private const val TITLE_EXTRA = "title_extra"
        private const val TEXT_EXTRA = "text_extra"

        @JvmStatic
        @JvmOverloads
        fun createIntent(
            context: Context,
            actionType: ACTION,
            notificationId: Int,
            wordId: Long? = null,
            newNtfTitle: String? = null,
            newNtfText: String? = null,
        ): Intent {
            return Intent(context, NotificationActionReceiver::class.java).apply {
                putExtra(ACTION_EXTRA, actionType)
                putExtra(NTF_ID_EXTRA, notificationId)
                putExtra(WORD_ID_EXTRA, wordId)
                putExtra(TITLE_EXTRA, newNtfTitle)
                putExtra(TEXT_EXTRA, newNtfText)
            }
        }
    }
}

enum class ACTION {
    SHOW, KNOWN, OK
}