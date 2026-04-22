package com.example.pexelsapp.presentation.features.notifications

import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationScheduler @Inject constructor(
    private val workManager: WorkManager
) {
    fun scheduleDailyReminder() {
        val workRequest = PeriodicWorkRequestBuilder<ReminderWorker>(
            24, TimeUnit.HOURS,
            1, TimeUnit.HOURS
        ).build()

        workManager.enqueueUniquePeriodicWork(
            REMINDER_WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
    }

    fun cancelAllReminders() {
        workManager.cancelUniqueWork(REMINDER_WORK_NAME)
    }

    companion object {
        private const val REMINDER_WORK_NAME = "daily_photo_reminder"
    }
}
