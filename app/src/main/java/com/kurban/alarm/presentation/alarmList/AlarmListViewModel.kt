package com.kurban.alarm.presentation.alarmList

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kurban.alarm.domain.model.Alarm
import com.kurban.alarm.domain.usecase.DeleteAlarmUseCase
import com.kurban.alarm.domain.usecase.GetAllAlarmsUseCase
import com.kurban.alarm.domain.usecase.ToggleAlarmUseCase
import com.kurban.alarm.notification.AlarmScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AlarmListState(
    val alarms: List<Alarm> = emptyList(),
    val isLoading: Boolean = true
)

@HiltViewModel
class AlarmListViewModel @Inject constructor(
    private val getAllAlarmsUseCase: GetAllAlarmsUseCase,
    private val toggleAlarmUseCase: ToggleAlarmUseCase,
    private val deleteAlarmUseCase: DeleteAlarmUseCase,
    private val alarmScheduler: AlarmScheduler
) : ViewModel() {

    private val _state = MutableStateFlow(AlarmListState())
    val state: StateFlow<AlarmListState> = _state.asStateFlow()

    init {
        loadAlarms()
    }

    private fun loadAlarms() {
        viewModelScope.launch {
            getAllAlarmsUseCase().collect { alarms ->
                _state.value = _state.value.copy(
                    alarms = alarms,
                    isLoading = false
                )
            }
        }
    }

    fun toggleAlarm(alarm: Alarm) {
        viewModelScope.launch {
            toggleAlarmUseCase(alarm)
            val newEnabledState = !alarm.isEnabled
            if (newEnabledState) {
                alarmScheduler.schedule(alarm.copy(isEnabled = true))
            } else {
                alarmScheduler.cancel(alarm)
            }
        }
    }

    fun deleteAlarm(alarm: Alarm) {
        viewModelScope.launch {
            alarmScheduler.cancel(alarm)
            deleteAlarmUseCase(alarm)
        }
    }
}
