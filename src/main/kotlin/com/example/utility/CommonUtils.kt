package com.example.utility

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

object CommonUtils {
    var datPatternForIrctc = "yyyy-MM-dd"
    var datePatternForTrainman = "dd-MM-yyyy"
    var simpleDateFormatForIrctc: DateFormat = SimpleDateFormat(datPatternForIrctc)
    var simpleDateFormatForTrainMan: DateFormat = SimpleDateFormat(datePatternForTrainman)
    var date = Date()
    private var c: Calendar = Calendar.getInstance()

    fun dateFormatForIrctc(dayOffset: Int): String {
        c.time = date
        c.add(Calendar.DATE, dayOffset)
        c.get(Calendar.DAY_OF_WEEK)
        val dateString: String = simpleDateFormatForIrctc.format(c.time)
        return dateString.replace("-", "")
    }

    fun dateFormatForTrainMan(dayOffset: Int): String {
        c.time = date
        c.add(Calendar.DATE, dayOffset)
        c.get(Calendar.DAY_OF_WEEK)
        val newDate: Date = c.time
        return simpleDateFormatForTrainMan.format(newDate)
    }


    fun getDay(): String {
        var dayIndex: Int = c.get(Calendar.DAY_OF_WEEK)
        dayIndex = (dayIndex + 5) % 7
        val dayList = ArrayList<String>()
        dayList.add("runningMon")
        dayList.add("runningTue")
        dayList.add("runningWed")
        dayList.add("runningThu")
        dayList.add("runningFri")
        dayList.add("runningSat")
        dayList.add("runningSun")
        return dayList[dayIndex]
    }

    fun doesRunWithTM(): Int {
        var dayIndex: Int = c.get(Calendar.DAY_OF_WEEK)
        dayIndex = (dayIndex + 5) % 7
        return dayIndex
    }

    fun getTrainListIRCTC(fromStation: String, toStation: String, date: String): String? {
        return """
            {
            "handicapFlag": false,
            "srcStn": "$fromStation",
            "jrnyDate": "$date",
            "jrnyClass": "",
            "ftBooking": false,
            "loyaltyRedemptionBooking": false,
            "ticketType": "E",
            "quotaCode": "GN",
            "concessionBooking": false,
            "destStn": "$toStation",
            "flexiFlag": true,
            "currentBooking": false
            }
            """.trimIndent()
    }
}