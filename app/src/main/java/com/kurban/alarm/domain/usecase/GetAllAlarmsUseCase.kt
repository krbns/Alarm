package com.kurban.alarm.domain.usecase

import com.kurban.alarm.domain.model.Alarm
import com.kurban.alarm.domain.repository.AlarmRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllAlarmsUseCase @Inject constructor(
    private val repository: AlarmRepository
) {
    operator fun invoke(): Flow<List<Alarm>> = repository.getAllAlarms()
}
