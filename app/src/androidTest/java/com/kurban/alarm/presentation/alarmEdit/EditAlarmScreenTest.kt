package com.kurban.alarm.presentation.alarmEdit

import android.Manifest
import androidx.activity.compose.setContent
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.rule.GrantPermissionRule
import com.kurban.alarm.presentation.MainActivity
import com.kurban.alarm.presentation.theme.AlarmTheme
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.DayOfWeek

class EditAlarmScreenTest {

    @get:Rule
    val permissionRule: GrantPermissionRule = GrantPermissionRule.grant(
        Manifest.permission.POST_NOTIFICATIONS
    )

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    private var labelValue = ""
    private var selectedDay: DayOfWeek? = null
    private var saveClicked = false
    private var backClicked = false
    private var vibrateToggled = false
    private var timeClicked = false

    @Before
    fun setup() {
        composeTestRule.activity.setContent {
            AlarmTheme {
                EditAlarmContent(
                    state = EditAlarmState(),
                    onTimeClick = { timeClicked = true },
                    alarmId = -1L,
                    onNavigateBack = { backClicked = true },
                    updateLabel = { labelValue = it },
                    saveAlarm = { saveClicked = true },
                    toggleDay = { selectedDay = it },
                    toggleVibrate = { vibrateToggled = true }
                )
            }
        }
    }

    @After
    fun tearDown() {
        // Сброс состояний после каждого теста
        labelValue = ""
        selectedDay = null
        saveClicked = false
        backClicked = false
        vibrateToggled = false
        timeClicked = false
    }

    @Test
    fun editAlarmScreen_showsTitleNewAlarm_forNewAlarm() {
        composeTestRule.onNodeWithText("Новый будильник").assertIsDisplayed()
    }

    @Test
    fun editAlarmScreen_showsTitleEdit_forExistingAlarm() {
        composeTestRule.activity.setContent {
            AlarmTheme {
                EditAlarmContent(
                    state = EditAlarmState(alarmId = 1),
                    onTimeClick = { timeClicked = true },
                    alarmId = 1L,
                    onNavigateBack = { backClicked = true },
                    updateLabel = { labelValue = it },
                    saveAlarm = { saveClicked = true },
                    toggleDay = { selectedDay = it },
                    toggleVibrate = { vibrateToggled = true }
                )
            }
        }
        composeTestRule.onNodeWithText("Редактировать").assertIsDisplayed()
    }

    @Test
    fun editAlarmScreen_showsDefaultTime() {
        composeTestRule.onNodeWithText("08:00").assertIsDisplayed()
    }

    @Test
    fun editAlarmScreen_updateLabel_callsCallback() {
        composeTestRule.onNodeWithText("Название")
            .performTextInput("Утренняя тренировка")

        assertEquals("Утренняя тренировка", labelValue)
    }

    @Test
    fun editAlarmScreen_toggleDay_callsCallback() {
        composeTestRule.onNodeWithText("M").performClick()
        composeTestRule.waitForIdle()
        assertEquals(DayOfWeek.MONDAY, selectedDay)
    }

    @Test
    fun editAlarmScreen_saveButton_callsCallback() {
        composeTestRule.onNodeWithText("Сохранить").performClick()
        composeTestRule.waitForIdle()
        assertTrue(saveClicked)
    }

    @Test
    fun editAlarmScreen_backButton_callsCallback() {
        composeTestRule.onNodeWithContentDescription("Назад").performClick()
        composeTestRule.waitForIdle()
        assertTrue(backClicked)
    }

    @Test
    fun editAlarmScreen_timeClick_callsCallback() {
        composeTestRule.onNodeWithText("08:00").performClick()
        composeTestRule.waitForIdle()
        assertTrue(timeClicked)
    }

    @Test
    fun editAlarmScreen_showsSelectedDays() {
        // Пересоздаем с выбранными днями для проверки отображения
        composeTestRule.activity.setContent {
            AlarmTheme {
                EditAlarmContent(
                    state = EditAlarmState(
                        repeatDays = setOf(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY)
                    ),
                    onTimeClick = { timeClicked = true },
                    alarmId = -1L,
                    onNavigateBack = { backClicked = true },
                    updateLabel = { labelValue = it },
                    saveAlarm = { saveClicked = true },
                    toggleDay = { selectedDay = it },
                    toggleVibrate = { vibrateToggled = true }
                )
            }
        }

        composeTestRule.onNodeWithText("M").assertIsDisplayed()
        composeTestRule.onNodeWithText("W").assertIsDisplayed()
    }
}
