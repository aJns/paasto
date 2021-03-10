package fi.nikulaj.paasto

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.Worker
import androidx.work.WorkerParameters

class NotificationWorker(appContext: Context, workerParams: WorkerParameters): CoroutineWorker(appContext, workerParams) {

    private val model = MainModel.getModelInstance(appContext)

    override suspend fun doWork(): Result {
        checkReminders()

        return Result.success()
    }

    private suspend fun checkReminders() {
        Log.d("asd", "reminders")

        if (inputData.getBoolean(NOTIFY_FAST_END, false)) {
            Log.d("qwerty", "check fast end")

            if (model.fastTargetReached()) {
                Log.d("workkeri", "fast ended")
            }
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