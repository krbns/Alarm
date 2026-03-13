package com.kurban.alarm.domain.usecase

import com.kurban.alarm.domain.model.Alarm
import com.kurban.alarm.domain.repository.AlarmRepository
import javax.inject.Inject

class ToggleAlarmUseCase @Inject constructor(
    private val repository: AlarmRepository
) {
    suspend operator fun invoke(alarm: Alarm) = repository.toggleAlarm(alarm)
}
