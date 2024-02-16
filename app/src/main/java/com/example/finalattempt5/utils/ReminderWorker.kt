package com.example.finalattempt5.utils

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.finalattempt5.NotificationHelper

class ReminderWorker(val context: Context, val params: WorkerParameters) : Worker(context, params){
    override fun doWork(): Result {
        NotificationHelper(context).createNotification(
            inputData.getString("title").toString(),
            inputData.getString("message").toString(),
            inputData.getString("notification_id").toString())
        return Result.success()
    }
}
