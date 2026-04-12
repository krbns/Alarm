package com.kurban.alarm.presentation.mathChallenge

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kurban.alarm.domain.model.MathChallenge
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MathChallengeViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow(MathChallengeState())
    val state: StateFlow<MathChallengeState> = _state.asStateFlow()

    private var timerJob: Job? = null

    var onCompleted: (() -> Unit)? = null
    var onTimerExpired: (() -> Unit)? = null

    init {
        startTimer()
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_state.value.remainingSeconds > 0 && !_state.value.isCompleted) {
                delay(1000)
                _state.update { it.copy(remainingSeconds = it.remainingSeconds - 1) }
            }
            if (_state.value.remainingSeconds == 0 && !_state.value.isCompleted) {
                _state.update { it.copy(isTimerExpired = true) }
                onTimerExpired?.invoke()
            }
        }
    }

    fun onDigitPressed(digit: String) {
        if (_state.value.userAnswer.length < 3) {
            _state.update { it.copy(userAnswer = it.userAnswer + digit, isWrongAnswer = false) }
        }
    }

    fun onBackspacePressed() {
        if (_state.value.userAnswer.isNotEmpty()) {
            _state.update { it.copy(userAnswer = it.userAnswer.dropLast(1), isWrongAnswer = false) }
        }
    }

    fun onEnterPressed() {
        val currentState = _state.value
        val userAnswer = currentState.userAnswer.toIntOrNull()

        if (userAnswer == currentState.currentTask.correctAnswer) {
            val newSolvedCount = currentState.solvedCount + 1
            if (newSolvedCount >= MathChallengeState.TOTAL_TASKS) {
                _state.update { it.copy(solvedCount = newSolvedCount, isCompleted = true) }
                timerJob?.cancel()
                onCompleted?.invoke()
            } else {
                _state.update {
                    it.copy(
                        solvedCount = newSolvedCount,
                        userAnswer = "",
                        currentTask = MathChallenge.generate(),
                        isWrongAnswer = false
                    )
                }
            }
        } else {
            _state.update {
                it.copy(
                    userAnswer = "",
                    currentTask = MathChallenge.generate(),
                    isWrongAnswer = true
                )
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}