package com.kurban.alarm.presentation.alarm

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.kurban.alarm.presentation.theme.AlarmTheme
import com.kurban.alarm.presentation.theme.spacing
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Composable
fun AlarmRingScreen(
    label: String,
    onDismiss: () -> Unit,
    onSnooze: () -> Unit
) {
    val currentTime = remember {
        LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"))
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = currentTime,
                fontSize = 96.sp,
                fontWeight = FontWeight.Light
            )

            if (label.isNotEmpty()) {
                Text(
                    text = label,
                    fontSize = 24.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.doubleExtraLarge))

            Row(
                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.extraLarge)
            ) {
                Button(
                    onClick = onSnooze,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary
                    )
                ) {
                    Text("+5 минут", modifier = Modifier.padding(horizontal = MaterialTheme.spacing.large, vertical = MaterialTheme.spacing.small))
                }

                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text("Отключить", modifier = Modifier.padding(horizontal = MaterialTheme.spacing.large, vertical = MaterialTheme.spacing.small))
                }
            }
        }
    }
}


@Preview
@Composable
private fun AlarmScreenPreview() {
    AlarmTheme {
        AlarmRingScreen(
            label = "Label",
            onDismiss = { },
            onSnooze = { },
        )
    }
}