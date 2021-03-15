package fi.nikulaj.paasto

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.SystemClock
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room


const val REMINDER_WORK_REQUEST = "reminderWorkRequest"

class ReminderManager(private val activity: AppCompatActivity) {
    var notifyFastEnd: Boolean? = null
        get() {
            field = getPreferenceField(field, R.string.notify_fast_end, false)
            return field
        }
        set(value) {
            field = setPreferenceField(field, value!!, R.string.notify_fast_end)
        }

    var notifyFastStart: Boolean? = null
        get() = getPreferenceField(field, R.string.notify_fast_start, false)
        set(value) {
            field = setPreferenceField(field, value!!, R.string.notify_fast_start)
        }

    var feedingTimeDuration: Int? = null
        get() = getPreferenceField(field, R.string.feeding_time_length, 6)
        set(value) {
            field = setPreferenceField(field, value!!, R.string.feeding_time_length)
        }

    private inline fun <reified T : Any> getPreferenceField(fieldIn: T?, id: Int, default: T): T? {
        var field = fieldIn
        if (field == null) {
            val sharedPref = activity.getPreferences(Context.MODE_PRIVATE)
            field = when (T::class) {
                Boolean::class -> sharedPref.getBoolean(
                        activity.getString(id),
                        default as Boolean
                ) as T?
                Int::class -> sharedPref.getInt(activity.getString(id), default as Int) as T?
                else -> null
            }
        }
        return field
    }

    private inline fun <reified T : Any> setPreferenceField(fieldIn: T?, value: T?, id: Int): T? {
        var field = fieldIn
        if (field != value && value != null) {
            field = value
            val sharedPref = activity.getPreferences(Context.MODE_PRIVATE)
            when (T::class) {
                Boolean::class -> {
                    with(sharedPref.edit()) {
                        putBoolean(activity.getString(id), value as Boolean)
                        apply()
                    }
                }
                Int::class -> {
                    with(sharedPref.edit()) {
                        putInt(activity.getString(id), value as Int)
                        apply()
                    }
                }
                else -> Unit
            }
        }
        return field
    }

    fun scheduleNotifications(type: NotificationType, showAt: Long, notificationInfo: String?) {
        Log.d("ReminderManager", "Scheduling notification...")

        notifyFastEnd = true

        when(type) {
            NotificationType.FastTargetReached -> if (notifyFastEnd != true) return
            NotificationType.TimeSinceLastFast -> if (notifyFastStart != true) return
        }

        val alarmMgr = activity.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val alarmIntent = Intent(activity, NotificationPublisher::class.java).let { intent ->
            intent.putExtra(NotificationPublisher.NOTIFY_TYPE, type.type)
            intent.putExtra(NotificationPublisher.NOTIFY_INFO, notificationInfo)
            PendingIntent.getBroadcast(activity, 0, intent, 0)
        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            alarmMgr.setAndAllowWhileIdle(AlarmManager.ELAPSED_REALTIME_WAKEUP, showAt, alarmIntent)
        } else {
            alarmMgr.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, showAt, alarmIntent)
        }
        Log.d("ReminderManager", "Scheduled.")
    }
}