package com.yuchen.makeplan.util

import com.yuchen.makeplan.DAY_MILLIS
import com.yuchen.makeplan.HOUR_MILLIS
import com.yuchen.makeplan.MINUTE_MILLIS
import java.text.SimpleDateFormat
import java.util.*

object TimeUtil {
    fun taskDate(time: Long): String {
        val simpleDateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm")
        return simpleDateFormat.format(Date(time))
    }

    fun timeDurationToString(start : Long, end : Long) : String{
        var duration = end - start
        var days : Int = (duration/DAY_MILLIS).toInt()
        duration %= DAY_MILLIS
        var hours : Int = (duration/ HOUR_MILLIS).toInt()
        duration%= HOUR_MILLIS
        var mins : Int = (duration/ MINUTE_MILLIS).toInt()

        return "$days D $hours H $mins M"
    }

    fun millisToGanttToolBarTime(time: Long) : String{
        val simpleDateFormat = SimpleDateFormat("EEE MM/dd HH:mm")
        return simpleDateFormat.format(Date(time))
    }


    @JvmStatic
    fun StampToDate(time: Long): String {
        // 進來的time以秒為單位，Date輸入為毫秒為單位，要注意

        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

        return simpleDateFormat.format(Date(time))
    }

    @JvmStatic
    fun DateToStamp(date: String): Long {
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

        /// 輸出為毫秒為單位
        return simpleDateFormat.parse(date).time
    }

    fun millisToYearMonthDay(time: Long): String {
        val simpleDateFormat = SimpleDateFormat("yyyy/MMM/dd")
        return simpleDateFormat.format(Date(time))
    }

    fun millisToYearMonth(time: Long): String {
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

    fun millisToMonth(time: Long): String {
        val simpleDateFormat = SimpleDateFormat("MMM")
        return simpleDateFormat.format(Date(time))
    }

    fun millisToHour(time: Long): String {
        val simpleDateFormat = SimpleDateFormat("HH")
        return simpleDateFormat.format(Date(time))
    }
}