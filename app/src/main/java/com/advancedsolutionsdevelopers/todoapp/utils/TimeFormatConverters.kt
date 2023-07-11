package com.advancedsolutionsdevelopers.todoapp.utils

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
//перевод времени
class TimeFormatConverters {
    val zoneId: ZoneId by lazy { ZoneId.of(ZoneOffset.systemDefault().id) }

    fun dateToTimestamp(date: LocalDateTime): Long {
        return date.toEpochSecond(zoneId.rules.getOffset(LocalDateTime.now()))
    }

    private fun fromTimestamp(value: Long): LocalDateTime {
        return LocalDateTime.ofEpochSecond(value, 0, zoneId.rules.getOffset(LocalDateTime.now()))
    }

    fun fromTimeStampToDate(value: Long): LocalDate {
        return fromTimestamp(value).toLocalDate()
    }
}