import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import fi.nikulaj.paasto.R
import kotlin.reflect.KClass

class ReminderManager(private val activity: AppCompatActivity) {

    var notifyFastEnd: Boolean? = null
        get() = getPreferenceField(field, R.string.notify_fast_end, false)
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

    private inline fun <reified T: Any> getPreferenceField(fieldIn: T?, id: Int, default: T): T? {
        var field = fieldIn
        if (field == null) {
            val sharedPref = activity.getPreferences(Context.MODE_PRIVATE)
            field = when(T::class) {
                Boolean::class -> sharedPref.getBoolean(activity.getString(id), default as Boolean) as T
                Int::class -> sharedPref.getInt(activity.getString(id), default as Int) as T
                else -> null
            }
        }
        return field
    }

    private inline fun <reified T: Any> setPreferenceField(fieldIn: T?, value: T?, id: Int): T? {
        var field = fieldIn
        if (field != value && value != null) {
            field = value
            val sharedPref = activity.getPreferences(Context.MODE_PRIVATE)
            when(T::class) {
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
}
}