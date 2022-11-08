package com.halo.dictionary.periodic

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.util.Log
import android.widget.Toast
import com.halo.dictionary.mvp.WordEntry
import com.halo.dictionary.repository.DictionaryRepository
import com.halo.dictionary.repository.PreferencesHelper
import java.time.LocalTime
import java.util.*

object NotificationsSeriesChallenge {
    private const val SERIES_SIZE = 3
    private var counter = 0

    fun onKnown(
        context: Context,
        navigator: DictionaryRepository.Navigator,
        preferencesHelper: PreferencesHelper,
        notificationManager: NotificationManager
    ) {
        if (counter < SERIES_SIZE - 1) {
            counter++
            // todo pg do not show, but update current ntf!!!!
            showNextNotification(context, navigator, preferencesHelper, notificationManager)
        } else if (counter == SERIES_SIZE - 1) {
            counter = 0
            showRewardsToast(context)
        } else {
            counter = 0
            // todo pg log warn
        }
    }

    private fun showRewardsToast(context: Context) {
        Toast.makeText(context, "Great!", Toast.LENGTH_SHORT).show()
    }

    // todo pg
    private fun showNextNotification(
        context: Context,
        navigator: DictionaryRepository.Navigator,
        preferencesHelper: PreferencesHelper,
        notificationManager: NotificationManager
    ) {
        if (navigator.getEntriesAmount() > 0 && !PeriodicWorkUtils.userMightBeSleeping(
                LocalTime.now()
            )
        ) {
            for (i in 0 until 1) {
                val randomIndex = PeriodicWorkUtils.getNextRandomIndex(
                    navigator.getEntriesAmount(),
                    preferencesHelper
                )
                if (randomIndex.isPresent) {
                    val entryOpt: Optional<WordEntry> =
                        navigator.getEntryByIndex(randomIndex.asInt)
                    if (!entryOpt.isPresent) {
                        Log.e(
                            "series",
                            "Entry for index " + randomIndex.asInt + " not found"
                        )
                    }
                    entryOpt.ifPresent { entry: WordEntry ->
                        sendWordNotification(
                            context,
                            entry,
                            i,
                            notificationManager
                        )
                    }
                }
            }
        }
    }

    private fun sendWordNotification(
        context: Context,
        wordEntry: WordEntry,
        notificationOrderNumber: Int,
        notificationManager: NotificationManager
    ) {
        if (notificationManager.getNotificationChannel(NTF_CHANNEL) == null) {
            val channel = NotificationChannel(
                NTF_CHANNEL, NTF_CHANNEL, NotificationManager.IMPORTANCE_DEFAULT
            )
            channel.description = "Channel to display the word to remember notifications"
            notificationManager.createNotificationChannel(channel)
        }
        val notificationId = 100 + notificationOrderNumber
        val notification = buildNotification(context, wordEntry, notificationId)
        if (notification == null) {
            Log.w("WordOfTheDayPeriodicWork.TAG", "Notification won't be send")
            return
        }
        notificationManager.notify(notificationId, notification)
    }

    private fun buildNotification(context: Context, wordEntry: WordEntry, notificationId: Int): Notification? {
        val id = wordEntry.id
        if (id == null) {
            Log.w("WordOfTheDayPeriodicWork.TAG", "Entry id is null")
            return null
        }
        val isReverseTranslateDirection = Random().nextBoolean()
        val word = if (isReverseTranslateDirection) wordEntry.translation else wordEntry.word
        return buildNotification(
            context,
            "Do you know what is it?",
            word,
            notificationId,
            Arrays.asList(Known(context, id), Show(context, wordEntry, isReverseTranslateDirection))
        )
    }

    fun onOk() {
        counter = 0
    }

    fun onShow() {
        counter = 0
    }
}