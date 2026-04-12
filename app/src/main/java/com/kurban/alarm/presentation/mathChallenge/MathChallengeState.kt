package com.kurban.alarm.presentation.mathChallenge

import com.kurban.alarm.domain.model.MathChallenge

data class MathChallengeState(
    val currentTask: MathChallenge = MathChallenge.generate(),
    val userAnswer: String = "",
    val solvedCount: Int = 0,
    val remainingSeconds: Int = TIMER_DURATION,
    val isCompleted: Boolean = false,
    val isWrongAnswer: Boolean = false,
    val isTimerExpired: Boolean = false
) {
    companion object {
        const val TOTAL_TASKS = 3
        const val TIMER_DURATION = 300
    }
}