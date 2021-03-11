package fi.nikulaj.paasto

import android.app.Activity
import java.text.SimpleDateFormat
import java.util.*

val dateFmtr = SimpleDateFormat.getDateInstance(SimpleDateFormat.SHORT)
val timeFmtr = SimpleDateFormat.getTimeInstance(SimpleDateFormat.SHORT)

fun millisToHMS(millis: Long): Triple<Long, Long, Long> {

    val seconds = millis / 1_000
    val minutes = seconds / 60
    val hours = minutes / 60

    val leftOverSecs = seconds % 60
    val leftOverMins = minutes % 60

    return Triple(hours, leftOverMins, leftOverSecs)
}

fun getDateStringFromMillis(activity: Activity, millis: Long?): String {
    return if (millis != null) {
        val cal = Calendar.getInstance()
        cal.timeInMillis = millis

        dateFmtr.format(cal.time) + " " + timeFmtr.format(cal.time)
    } else {
        activity.getString(R.string.num_invalid)
    }
}

fun getTimeStringFromMillis(millis: Long): String {
    val cal = Calendar.getInstance()
    cal.timeInMillis = millis

    return timeFmtr.format(cal.time)
}

fun getDayMonthFromMillis(millis: Long): Pair<Int, Int> {
    val cal = Calendar.getInstance()
    cal.timeInMillis = millis

    val day = cal.get(Calendar.DAY_OF_MONTH)
    val month = cal.get(Calendar.MONTH)

    return Pair(day, month)
}