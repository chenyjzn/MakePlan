package com.yuchen.makeplan.util

import java.text.SimpleDateFormat
import java.util.*

object TimeUtil {
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

}