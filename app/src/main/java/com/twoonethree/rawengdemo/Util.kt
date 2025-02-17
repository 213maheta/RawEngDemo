package com.twoonethree.rawengdemo

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

object Util {
    @RequiresApi(Build.VERSION_CODES.O)
    fun formatMonth(date: String, format: String): String {
        val inputDate = LocalDate.parse(date)
        val outputFormatter = DateTimeFormatter.ofPattern(format).withLocale(Locale.getDefault())
        return inputDate.format(outputFormatter).uppercase()
    }
}