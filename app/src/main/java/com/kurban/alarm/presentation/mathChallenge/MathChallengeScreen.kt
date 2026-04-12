package com.kurban.alarm.presentation.mathChallenge

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kurban.alarm.domain.model.MathOperator

@Composable
fun MathChallengeScreen(
    onDismiss: () -> Unit,
    onTimerExpired: () -> Unit,
    viewModel: MathChallengeViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(state.isCompleted) {
        if (state.isCompleted) {
            onDismiss()
        }
    }

    LaunchedEffect(state.isTimerExpired) {
        if (state.isTimerExpired) {
            onTimerExpired()
        }
    }

    MathChallengeContent(
        state = state,
        onDigitPressed = viewModel::onDigitPressed,
        onBackspacePressed = viewModel::onBackspacePressed,
        onEnterPressed = viewModel::onEnterPressed
    )
}

@Composable
fun MathChallengeContent(
    state: MathChallengeState,
    onDigitPressed: (String) -> Unit,
    onBackspacePressed: () -> Unit,
    onEnterPressed: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TimerDisplay(remainingSeconds = state.remainingSeconds)

        Spacer(modifier = Modifier.height(48.dp))

        TaskDisplay(state = state)

        Spacer(modifier = Modifier.height(32.dp))

        AnswerDisplay(
            answer = state.userAnswer,
            isWrongAnswer = state.isWrongAnswer
        )

        Spacer(modifier = Modifier.height(24.dp))

        ProgressIndicator(
            solvedCount = state.solvedCount,
            totalTasks = MathChallengeState.TOTAL_TASKS
        )

        Spacer(modifier = Modifier.weight(1f))

        NumberPad(
            onDigitPressed = onDigitPressed,
            onBackspacePressed = onBackspacePressed,
            onEnterPressed = onEnterPressed
        )

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun TimerDisplay(remainingSeconds: Int) {
    val minutes = remainingSeconds / 60
    val seconds = remainingSeconds % 60

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.primaryContainer)
            .padding(horizontal = 24.dp, vertical = 12.dp)
    ) {
        Text(
            text = String.format("%02d:%02d", minutes, seconds),
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}

@Composable
private fun TaskDisplay(state: MathChallengeState) {
    val operatorSymbol = when (state.currentTask.operator) {
        MathOperator.PLUS -> "+"
        MathOperator.MINUS -> "−"
    }

    Text(
        text = "${state.currentTask.firstOperand} $operatorSymbol ${state.currentTask.secondOperand} = ?",
        fontSize = 48.sp,
        fontWeight = FontWeight.Medium,
        color = MaterialTheme.colorScheme.onBackground
    )
}

@Composable
private fun AnswerDisplay(answer: String, isWrongAnswer: Boolean) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(
                if (isWrongAnswer) MaterialTheme.colorScheme.errorContainer
                else MaterialTheme.colorScheme.surfaceVariant
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = answer.ifEmpty { "?" },
            fontSize = 36.sp,
            fontWeight = FontWeight.Light,
            color = when {
                answer.isEmpty() -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                isWrongAnswer -> MaterialTheme.colorScheme.error
                else -> MaterialTheme.colorScheme.onSurfaceVariant
            }
        )
    }
}

@Composable
private fun ProgressIndicator(solvedCount: Int, totalTasks: Int) {
    Text(
        text = "Решите еще ${totalTasks - solvedCount} задач${if (totalTasks - solvedCount == 1) "у" else ""}",
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

@Composable
private fun NumberPad(
    onDigitPressed: (String) -> Unit,
    onBackspacePressed: () -> Unit,
    onEnterPressed: () -> Unit
) {
    val digits = listOf(
        listOf("1", "2", "3"),
        listOf("4", "5", "6"),
        listOf("7", "8", "9"),
        listOf("C", "0", "OK")
    )

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        digits.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                row.forEach { key ->
                    when (key) {
                        "C" -> NumberPadButton(
                            text = "C",
                            onClick = onBackspacePressed,
                            modifier = Modifier.weight(1f),
                            isSpecial = true
                        )
                        "OK" -> NumberPadButton(
                            text = "OK",
                            onClick = onEnterPressed,
                            modifier = Modifier.weight(1f),
                            isPrimary = true
                        )
                        else -> NumberPadButton(
                            text = key,
                            onClick = { onDigitPressed(key) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun NumberPadButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isPrimary: Boolean = false,
    isSpecial: Boolean = false
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .height(64.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = when {
                isPrimary -> MaterialTheme.colorScheme.primary
                isSpecial -> MaterialTheme.colorScheme.secondary
                else -> MaterialTheme.colorScheme.surfaceVariant
            }
        )
    ) {
        Text(
            text = text,
            fontSize = 24.sp,
            fontWeight = FontWeight.Medium
        )
    }
}