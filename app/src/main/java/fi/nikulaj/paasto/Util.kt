package fi.nikulaj.paasto

import android.app.Activity
import java.text.SimpleDateFormat
import java.util.*

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
        val fmtr = SimpleDateFormat.getDateTimeInstance()

        fmtr.format(cal.time)
    } else {

        activity.getString(R.string.num_invalid)
    }
}
