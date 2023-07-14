package com.ranicorp.letswatchfootballtogether.util

import android.text.format.DateUtils
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

object DateFormatText {

    private const val DATE_YEAR_MONTH_DAY_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss"

    fun getCurrentDateString(): String {
        val date = Date()
        return SimpleDateFormat(DATE_YEAR_MONTH_DAY_TIME_PATTERN, SystemConfiguration.currentLocale).format(date)
    }

    fun longToDateString(time: Long): String {
        val date = Date(time)
        val format = SimpleDateFormat("yyyy년 MM월 dd일", SystemConfiguration.currentLocale)
        return format.format(date)
    }

    fun longToDisplayDate(time: Long): String {
        val format = SimpleDateFormat("MM.dd(E)", SystemConfiguration.currentLocale)
        return format.format(time)
    }

    fun longToHourMin(time: Long): String {
        val format = SimpleDateFormat("HH:mm", SystemConfiguration.currentLocale)
        return format.format(Date(time))
    }

    fun longToLastSentDate(time: Long): String? {
        return try {
            val date = Date(time)
            val newFormat = if (DateUtils.isToday(date.time)) {
                SimpleDateFormat("HH:mm", SystemConfiguration.currentLocale)
            } else {
                SimpleDateFormat("MM월 dd일", SystemConfiguration.currentLocale)
            }
            newFormat.format(date)
        } catch (e: ParseException) {
            null
        }
    }
}