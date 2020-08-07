package com.yuchen.makeplan.util

import com.yuchen.makeplan.DAY_MILLIS
import com.yuchen.makeplan.HOUR_MILLIS
import com.yuchen.makeplan.MINUTE_MILLIS
import java.text.SimpleDateFormat
import java.util.*

object TimeUtil {

    fun durationToToolBarString(start: Long, end: Long): String {
        var duration = end - start
        val day: Int = (duration / DAY_MILLIS).toInt()
        duration %= DAY_MILLIS
        val hour: Int = (duration / HOUR_MILLIS).toInt()
        duration %= HOUR_MILLIS
        val min: Int = (duration / MINUTE_MILLIS).toInt()

        return "$day D $hour H $min M"
    }

    fun millisToToolBarString(time: Long): String {
        val simpleDateFormat = SimpleDateFormat("EEE MM/dd HH:mm")
        return simpleDateFormat.format(Date(time))
    }

    fun millisToDate(time: Long): String {
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        return simpleDateFormat.format(Date(time))
    }

    fun millisToYearMonth2Day(time: Long): String {
        val simpleDateFormat = SimpleDateFormat("yyyy/MM/dd")
        return simpleDateFormat.format(Date(time))
    }

    fun millisToHourMinutes(time: Long): String {
        val simpleDateFormat = SimpleDateFormat("HH:mm")
        return simpleDateFormat.format(Date(time))
    }

    fun millisToYearMonth3Day(time: Long): String {
        val simpleDateFormat = SimpleDateFormat("yyyy/MMM/dd")
        return simpleDateFormat.format(Date(time))
    }

    fun millisToYearMonth3(time: Long): String {
        val simpleDateFormat = SimpleDateFormat("yyyy/MMM")
        return simpleDateFormat.format(Date(time))
    }

    fun millisToYear(time: Long): String {
        val simpleDateFormat = SimpleDateFormat("yyyy")
        return simpleDateFormat.format(Date(time))
    }

    fun millisToDay(time: Long): String {
        val simpleDateFormat = SimpleDateFormat("dd")
        return simpleDateFormat.format(Date(time))
    }

    fun millisToMonth3(time: Long): String {
        val simpleDateFormat = SimpleDateFormat("MMM")
        return simpleDateFormat.format(Date(time))
    }

    fun millisToHour(time: Long): String {
        val simpleDateFormat = SimpleDateFormat("HH")
        return simpleDateFormat.format(Date(time))
    }
}