package fi.nikulaj.paasto

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.Worker
import androidx.work.WorkerParameters

class NotificationPublisher: BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        // TODO: intentistä vois ottaa tarvittavat datat, eli lopetuskellonajan
        showNotification(context, 0)
    }

    private fun showNotification(context: Context, reachedAt: Long) {
        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent: PendingIntent = PendingIntent.getActivity(context, 0, intent, 0)

        val contentText = context.getString(R.string.notification_fast_end_content, getTimeStringFromMillis(reachedAt))

        val builder = NotificationCompat.Builder(context, context.getString(R.string.notification_channel_id))
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(context.getString(R.string.notification_fast_end_title))
                .setContentText(contentText)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)

        with(NotificationManagerCompat.from(context)) {
            notify(R.id.fast_end_notification_id, builder.build())
        }
    }

    companion object {
        const val NOTIFY_FAST_END = "notifyFastEnd"
        const val NOTIFY_FAST_START = "notifyFastStart"
        const val NOTIFY_FEEDING_DURATION = "notifyFeedingDuration"
    }
}