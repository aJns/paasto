package fi.nikulaj.paasto

import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.appcompat.app.AppCompatActivity


class ReminderManager(application: Application) {

    private val sharedPref = application.getSharedPreferences("reminder_manager_preferences", Context.MODE_PRIVATE)

    private val notifyFastEndId = "notify_fast_end"
    private val notifyFastStartId = "notify_fast_start"
    private val feedingTimeDurationId = "feeding_time_length"

    var notifyFastEnd: Boolean? = null
        get() = getPreferenceField(notifyFastEndId, false)
        set(value) {
            field = setPreferenceField(field, value!!, notifyFastEndId)
        }

    var notifyFastStart: Boolean? = null
        get() = getPreferenceField(notifyFastStartId, false)
        set(value) {
            field = setPreferenceField(field, value!!, notifyFastStartId)
        }

    var feedingTimeDuration: Int? = null
        get() = getPreferenceField(feedingTimeDurationId, 6)
        set(value) {
            field = setPreferenceField(field, value!!, feedingTimeDurationId)
        }

    private inline fun <reified T : Any> getPreferenceField(id: String, default: T): T? {
        return when (T::class) {
                Boolean::class -> sharedPref.getBoolean(
                        id,
                        default as Boolean
                ) as T?
                Int::class -> sharedPref.getInt(id, default as Int) as T?
                else -> null
            }
    }

    private inline fun <reified T : Any> setPreferenceField(fieldIn: T?, value: T?, id: String): T? {
        var field = fieldIn
        if (value != null) {
            field = value
            when (T::class) {
                Boolean::class -> {
                    with(sharedPref.edit()) {
                        putBoolean(id, value as Boolean)
                        apply()
                    }
                }
                Int::class -> {
                    with(sharedPref.edit()) {
                        putInt(id, value as Int)
                        apply()
                    }
                }
                else -> Unit
            }
        }
        return field
    }

    fun scheduleNotifications(activity: AppCompatActivity, type: NotificationType, showAt: Long, notificationInfo: String?) {
        Log.d("ReminderManager", "Scheduling notification...")

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
        // TODO: Alarms with equivalent intents replace the previous alarm, see intent filter doc for more

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            alarmMgr.setAndAllowWhileIdle(AlarmManager.ELAPSED_REALTIME_WAKEUP, showAt, alarmIntent)
        } else {
            alarmMgr.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, showAt, alarmIntent)
        }
        Log.d("ReminderManager", "Scheduled alarm for $showAt elapsed realtime.")
    }
}