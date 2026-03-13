package com.kurban.alarm.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.kurban.alarm.domain.model.Alarm
import java.time.DayOfWeek
import java.time.LocalTime

@Entity(tableName = "alarms")
data class AlarmEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val hour: Int,
    val minute: Int,
    val label: String,
    val isEnabled: Boolean,
    val repeatDays: String,
    val isVibrate: Boolean
) {
    fun toDomain(): Alarm {
        val days = if (repeatDays.isBlank()) {
            emptySet()
        } else {
            repeatDays.split(",").map { DayOfWeek.valueOf(it.trim()) }.toSet()
        }
        return Alarm(
            id = id,
            time = LocalTime.of(hour, minute),
            label = label,
            isEnabled = isEnabled,
            repeatDays = days,
            isVibrate = isVibrate
        )
    }

    companion object {
        fun fromDomain(alarm: Alarm): AlarmEntity {
            return AlarmEntity(
                id = alarm.id,
                hour = alarm.time.hour,
                minute = alarm.time.minute,
                label = alarm.label,
                isEnabled = alarm.isEnabled,
                repeatDays = alarm.repeatDays.joinToString(",") { it.name },
                isVibrate = alarm.isVibrate
            )
        }
    }
}
