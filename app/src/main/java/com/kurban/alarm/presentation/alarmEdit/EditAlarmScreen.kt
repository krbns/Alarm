package com.kurban.alarm.presentation.alarmEdit

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kurban.alarm.presentation.theme.AlarmTheme
import com.kurban.alarm.presentation.theme.spacing
import java.time.DayOfWeek
import java.time.format.TextStyle
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditAlarmScreen(
    alarmId: Long,
    onNavigateBack: () -> Unit,
) {
    val viewModel: EditAlarmViewModel = hiltViewModel()

    val state by viewModel.state.collectAsStateWithLifecycle()
    var showTimePicker by remember { mutableStateOf(false) }

    LaunchedEffect(alarmId) {
        viewModel.loadAlarm(alarmId)
    }

    LaunchedEffect(state.isSaved) {
        if (state.isSaved) {
            onNavigateBack()
        }
    }

    EditAlarmContent(
        state = state,
        onTimeClick = { showTimePicker = true},
        alarmId = alarmId,
        onNavigateBack = onNavigateBack,
        updateLabel = viewModel::updateLabel,
        saveAlarm = viewModel::saveAlarm,
        toggleDay = viewModel::toggleDay,
        toggleVibrate = viewModel::toggleVibrate
    )

    if (showTimePicker) {
        TimePickerDialog(
            initialHour = state.time.hour,
            initialMinute = state.time.minute,
            onDismiss = { showTimePicker = false },
            onConfirm = { hour, minute ->
                viewModel.updateTime(hour, minute)
                showTimePicker = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditAlarmContent(
    state: EditAlarmState,
    onTimeClick: () -> Unit,
    alarmId: Long,
    onNavigateBack: () -> Unit,
    updateLabel: (String) -> Unit,
    saveAlarm: () -> Unit,
    toggleDay: (DayOfWeek) -> Unit,
    toggleVibrate: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (alarmId == -1L) "Новый будильник" else "Редактировать") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                    }
                },
                actions = {
                    IconButton(onClick = { saveAlarm() }) {
                        Icon(Icons.Default.Check, contentDescription = "Сохранить")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(MaterialTheme.spacing.doubleExtraLarge))

            Text(
                text = String.format("%02d:%02d", state.time.hour, state.time.minute),
                fontSize = 72.sp,
                fontWeight = FontWeight.Light,
                modifier = Modifier.clickable { onTimeClick() }
            )

            if (state.isTimeInPast) {
                Text(
                    text = "Время уже прошло - будильник сработает завтра",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            } else {
                Text(
                    text = "Нажмите на время для изменения",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.doubleExtraLarge))

            OutlinedTextField(
                value = state.label,
                onValueChange = { updateLabel(it) },
                label = { Text("Название") },
                placeholder = { Text("Например, Подъём") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = MaterialTheme.spacing.large),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.large))

            Text(
                text = "Повтор",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = MaterialTheme.spacing.large)
            )

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.small))

            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small),
                contentPadding = PaddingValues(horizontal = MaterialTheme.spacing.large)
            ) {
                items(DayOfWeek.entries) { day ->
                    DayChip(
                        day = day,
                        isSelected = day in state.repeatDays,
                        onClick = { toggleDay(day) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.large))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = MaterialTheme.spacing.large),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Вибрация",
                    style = MaterialTheme.typography.titleMedium
                )
                Switch(
                    checked = state.isVibrate,
                    onCheckedChange = { toggleVibrate() }
                )
            }

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.doubleExtraLarge))

            Button(
                onClick = { saveAlarm() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = MaterialTheme.spacing.large)
            ) {
                Text("Сохранить")
            }
        }
    }
}

@Composable
private fun DayChip(
    day: DayOfWeek,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = if (isSelected) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.surfaceVariant
    }
    val contentColor = if (isSelected) {
        MaterialTheme.colorScheme.onPrimary
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }

    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(backgroundColor)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = day.getDisplayName(TextStyle.SHORT, Locale.getDefault()).first().toString(),
            color = contentColor,
            fontWeight = FontWeight.Medium
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TimePickerDialog(
    initialHour: Int,
    initialMinute: Int,
    onDismiss: () -> Unit,
    onConfirm: (Int, Int) -> Unit
) {
    val timePickerState = rememberTimePickerState(
        initialHour = initialHour,
        initialMinute = initialMinute,
        is24Hour = true
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = { onConfirm(timePickerState.hour, timePickerState.minute) }
            ) {
                Text("ОК")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Отмена")
            }
        },
        text = {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                TimePicker(state = timePickerState)
            }
        }
    )
}

@Preview
@Composable
private fun EditAlarmScreenPreview() {
    AlarmTheme {
        EditAlarmContent(
            state = EditAlarmState(),
            onTimeClick = {},
            alarmId = 0L,
            onNavigateBack = {},
            updateLabel = {},
            saveAlarm = {},
            toggleDay = {},
            toggleVibrate = {}
        )
    }
}