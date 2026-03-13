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
        var triggerDateTime = now.toLocalDate().atTime(time)

        if (!isRepeating) {
            if (triggerDateTime.isBefore(now)) {
                triggerDateTime = triggerDateTime.plusDays(1)
            }
        } else {
            var daysToAdd = 0
            val sortedDays = repeatDays.sorted()
            for (day in sortedDays) {
                val daysUntil = day.ordinal - now.dayOfWeek.ordinal
                if (daysUntil > 0) {
                    daysToAdd = daysUntil
                    break
                } else if (daysUntil == 0 && time.isAfter(now.toLocalTime())) {
                    daysToAdd = 0
                    break
                }
            }
            if (daysToAdd == 0) {
                for (day in sortedDays) {
                    val daysUntil = day.ordinal + 7 - now.dayOfWeek.ordinal
                    if (daysUntil > 0) {
                        daysToAdd = daysUntil
                        break
                    }
                }
            }
            triggerDateTime = triggerDateTime.plusDays(daysToAdd.toLong())
        }

        return triggerDateTime.atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli()
    }
}
