package fi.nikulaj.paasto

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.Worker
import androidx.work.WorkerParameters

class NotificationWorker(private val appContext: Context, workerParams: WorkerParameters): CoroutineWorker(appContext, workerParams) {

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
                showNotification(model.fastTargetReachedAt()!!)
            }
        }

        if (inputData.getBoolean(NOTIFY_FAST_START, false)) {
            Log.d("qwerty", "check fast start")
        }
    }

    private fun showNotification(reachedAt: Long) {
        val intent = Intent(appContext, MainActivity::class.java)
        val pendingIntent: PendingIntent = PendingIntent.getActivity(appContext, 0, intent, 0)

        val contentText = appContext.getString(R.string.notification_fast_end_content, getTimeStringFromMillis(reachedAt))

        val builder = NotificationCompat.Builder(appContext, appContext.getString(R.string.notification_channel_id))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(appContext.getString(R.string.notification_fast_end_title))
                .setContentText(contentText)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)

        with(NotificationManagerCompat.from(appContext)) {
            notify(R.id.fast_end_notification_id, builder.build())
        }
    }

    companion object {
        const val NOTIFY_FAST_END = "notifyFastEnd"
        const val NOTIFY_FAST_START = "notifyFastStart"
        const val NOTIFY_FEEDING_DURATION = "notifyFeedingDuration"
    }
}