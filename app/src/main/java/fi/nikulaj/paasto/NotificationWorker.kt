package fi.nikulaj.paasto

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters

class NotificationWorker(appContext: Context, workerParams: WorkerParameters): Worker(appContext, workerParams) {

    override fun doWork(): Result {
        checkReminders()

        return Result.success()
    }

    private fun checkReminders() {
        Log.d("asd", "reminders")

        if (inputData.getBoolean(NOTIFY_FAST_END, false)) {
            Log.d("qwerty", "check fast end")
        }

        if (inputData.getBoolean(NOTIFY_FAST_START, false)) {
            Log.d("qwerty", "check fast start")
        }
    }

    companion object {
        const val NOTIFY_FAST_END = "notifyFastEnd"
        const val NOTIFY_FAST_START = "notifyFastStart"
        const val NOTIFY_FEEDING_DURATION = "notifyFeedingDuration"
    }
}