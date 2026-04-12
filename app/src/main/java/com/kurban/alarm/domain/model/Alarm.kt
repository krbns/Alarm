package com.kurban.alarm.domain.model

import java.time.DayOfWeek
import java.time.LocalTime

data class Alarm(
    val id: Long = 0,
    val time: LocalTime,
    val label: String = "",
    val isEnabled: Boolean = true,
    val repeatDays: Set<DayOfWeek> = emptySet(),
    val isVibrate: Boolean = true
) {
    val isRepeating: Boolean get() = repeatDays.isNotEmpty()

    fun getNextTriggerTime(): Long {
        val now = java.time.LocalDateTime.now()
        val currentDayOfWeek = now.dayOfWeek

        if (!isRepeating) {
            var triggerDateTime = now.toLocalDate().atTime(time)
            if (triggerDateTime.isBefore(now)) {
                triggerDateTime = triggerDateTime.plusDays(1)
            }
            return triggerDateTime.atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli()
        }

        val sortedDays = repeatDays.sortedBy { it.ordinal }
        
        var daysToAdd: Long = 7

        for (day in sortedDays) {
            val daysDiff = day.ordinal - currentDayOfWeek.ordinal
            if (daysDiff > 0) {
                daysToAdd = daysDiff.toLong()
                break
            } else if (daysDiff == 0 && time.isAfter(now.toLocalTime())) {
                daysToAdd = 0
                break
            }
        }

        if (daysToAdd == 7L) {
            for (day in sortedDays) {
                val daysDiff = day.ordinal + 7 - currentDayOfWeek.ordinal
                if (daysDiff > 0) {
                    daysToAdd = daysDiff.toLong()
                    break
                }
            }
        }

        val triggerDateTime = now.toLocalDate().atTime(time).plusDays(daysToAdd)
        return triggerDateTime.atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli()
    }
}
