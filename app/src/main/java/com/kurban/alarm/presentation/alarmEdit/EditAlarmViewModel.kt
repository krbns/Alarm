package com.kurban.alarm.presentation.alarmEdit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kurban.alarm.domain.model.Alarm
import com.kurban.alarm.domain.usecase.GetAlarmByIdUseCase
import com.kurban.alarm.domain.usecase.SaveAlarmUseCase
import com.kurban.alarm.notification.AlarmScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalTime
import javax.inject.Inject

data class EditAlarmState(
    val alarmId: Long = 0,
    val time: LocalTime = LocalTime.of(8, 0),
    val label: String = "",
    val repeatDays: Set<DayOfWeek> = emptySet(),
    val isVibrate: Boolean = true,
    val isLoading: Boolean = false,
    val isSaved: Boolean = false
)

@HiltViewModel
class EditAlarmViewModel @Inject constructor(
    private val getAlarmByIdUseCase: GetAlarmByIdUseCase,
    private val saveAlarmUseCase: SaveAlarmUseCase,
    private val alarmScheduler: AlarmScheduler
) : ViewModel() {

    private val _state = MutableStateFlow(EditAlarmState())
    val state: StateFlow<EditAlarmState> = _state.asStateFlow()

    fun loadAlarm(alarmId: Long) {
        if (alarmId == -1L) return

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val alarm = getAlarmByIdUseCase(alarmId)
            alarm?.let {
                _state.update { state ->
                    state.copy(
                        alarmId = it.id,
                        time = it.time,
                        label = it.label,
                        repeatDays = it.repeatDays,
                        isVibrate = it.isVibrate,
                        isLoading = false
                    )
                }
            }
        }
    }

    fun updateTime(hour: Int, minute: Int) {
        _state.update { it.copy(time = LocalTime.of(hour, minute)) }
    }

    fun updateLabel(label: String) {
        _state.update { it.copy(label = label) }
    }

    fun toggleDay(day: DayOfWeek) {
        _state.update { state ->
            val newDays = if (day in state.repeatDays) {
                state.repeatDays - day
            } else {
                state.repeatDays + day
            }
            state.copy(repeatDays = newDays)
        }
    }

    fun toggleVibrate() {
        _state.update { it.copy(isVibrate = !it.isVibrate) }
    }

    fun saveAlarm() {
        viewModelScope.launch {
            val currentState = _state.value
            val alarm = Alarm(
                id = currentState.alarmId,
                time = currentState.time,
                label = currentState.label,
                repeatDays = currentState.repeatDays,
                isVibrate = currentState.isVibrate,
                isEnabled = true
            )
            val id = saveAlarmUseCase(alarm)
            val savedAlarm = alarm.copy(id = id)
            alarmScheduler.schedule(savedAlarm)
            _state.update { it.copy(isSaved = true) }
        }
    }
}
