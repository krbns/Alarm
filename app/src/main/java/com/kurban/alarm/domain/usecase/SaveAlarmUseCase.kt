package com.kurban.alarm.domain.usecase

import com.kurban.alarm.domain.model.Alarm
import com.kurban.alarm.domain.repository.AlarmRepository
import javax.inject.Inject

class SaveAlarmUseCase @Inject constructor(
    private val repository: AlarmRepository
) {
    suspend operator fun invoke(alarm: Alarm): Long {
        return if (alarm.id == 0L) {
            repository.insertAlarm(alarm)
        } else {
            repository.updateAlarm(alarm)
            alarm.id
        }
    }
}