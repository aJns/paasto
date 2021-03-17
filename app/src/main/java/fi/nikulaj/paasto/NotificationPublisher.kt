package fi.nikulaj.paasto

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

enum class NotificationType(val type: Int) {
    FastTargetReached(1),
    TimeSinceLastFast(2);

    companion object {
        fun getTypeFromInt(value: Int) = when (value) {
            1 -> FastTargetReached
            2 -> TimeSinceLastFast
            else -> TODO()
        }
    }
}

class NotificationPublisher : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        Log.d("NotificationPublisher", "It's notification time motherfucker")

        val type = NotificationType.getTypeFromInt(intent.getIntExtra(NOTIFY_TYPE, -1))
        val time = intent.getStringExtra(NOTIFY_INFO)

        showNotification(context, type, time)
    }

    private fun showNotification(context: Context, type: NotificationType, stringParam: String?) {
        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent: PendingIntent = PendingIntent.getActivity(context, 0, intent, 0)

        val (title, text) = when (type) {
            NotificationType.FastTargetReached -> {
                Pair(context.getString(R.string.notification_fast_end_title),
                        context.getString(R.string.notification_fast_end_content, stringParam))
            }
            NotificationType.TimeSinceLastFast -> {
                Pair(context.getString(R.string.notification_fast_start_title),
                        context.getString(R.string.notification_fast_start_content, stringParam))
            }
        }

        val builder = NotificationCompat.Builder(context, context.getString(R.string.notification_channel_id))
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(title)
                .setContentText(text)
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

        const val NOTIFY_TYPE = "notifyType"
        const val NOTIFY_INFO = "notifyTime"
    }
}