package com.example.pexelsapp.presentation.features.notifications

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class ReminderWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val notificationManager: AppNotificationManager
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        notificationManager.showReminderNotification()
        return Result.success()
    }
}
