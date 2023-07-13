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

    fun fromTimestamp(value: Long): LocalDateTime {
        return LocalDateTime.ofEpochSecond(value, 0, zoneId.rules.getOffset(LocalDateTime.now()))
    }

    fun plusDay(value: Long): Long {
        return dateToTimestamp(fromTimestamp(value).plusDays(1L))
    }
}