package com.advancedsolutionsdevelopers.todoapp.data

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
//перевод времени
class Converters {
    val zoneId: ZoneId by lazy { ZoneId.of(ZoneOffset.systemDefault().id) }

    fun dateToTimestamp(date: LocalDate): Long {
        return date.atStartOfDay(zoneId).toEpochSecond()
    }

    fun fromTimestamp(value: Long): LocalDateTime {
        return LocalDateTime.ofEpochSecond(value, 0, zoneId.rules.getOffset(LocalDateTime.now()))
    }

    fun fromTimeStampToDate(value: Long): LocalDate {
        return fromTimestamp(value).toLocalDate()
    }
}