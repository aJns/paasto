package fi.nikulaj.paasto

fun millisToHMS(millis: Long): Triple<Long, Long, Long> {

    val seconds = millis / 1_000
    val minutes = seconds / 60
    val hours = minutes / 60

    val leftOverSecs = seconds % 60
    val leftOverMins = minutes % 60

    return Triple(hours, leftOverMins, leftOverSecs)
}