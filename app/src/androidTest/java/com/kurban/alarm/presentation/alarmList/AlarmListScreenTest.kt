package com.kurban.alarm.presentation.alarmList

import android.Manifest
import androidx.activity.compose.setContent
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeLeft
import androidx.test.core.app.ApplicationProvider
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.GrantPermissionRule
import com.kurban.alarm.domain.model.Alarm
import com.kurban.alarm.presentation.MainActivity
import com.kurban.alarm.presentation.theme.AlarmTheme
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.DayOfWeek
import java.time.LocalTime

class AlarmListScreenTest {

    @get:Rule
    val permissionRule: GrantPermissionRule = GrantPermissionRule.grant(
        Manifest.permission.POST_NOTIFICATIONS
    )

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun grantPermissions() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val uiAutomation = instrumentation.uiAutomation

        listOf(
            Manifest.permission.POST_NOTIFICATIONS,
            Manifest.permission.VIBRATE
        ).forEach { permission ->
            uiAutomation.grantRuntimePermission(
                ApplicationProvider.getApplicationContext<android.app.Application>().packageName,
                permission
            )
        }
    }

    @Test
    fun alarmListScreen_showsLoadingIndicator_whenLoading() {
        composeTestRule.activity.setContent {
            AlarmTheme {
                AlarmListScreenContent(
                    state = AlarmListState(isLoading = true),
                    onNavigateToEdit = {},
                    toggleAlarm = {},
                    deleteAlarm = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Будильники").assertIsDisplayed()
    }

    @Test
    fun alarmListScreen_showsEmptyState_whenNoAlarms() {
        composeTestRule.activity.setContent {
            AlarmTheme {
                AlarmListScreenContent(
                    state = AlarmListState(isLoading = false, alarms = emptyList()),
                    onNavigateToEdit = {},
                    toggleAlarm = {},
                    deleteAlarm = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Нет будильников").assertIsDisplayed()
        composeTestRule.onNodeWithText("Нажмите + чтобы добавить").assertIsDisplayed()
    }

    @Test
    fun alarmListScreen_showsAlarmList_whenAlarmsExist() {
        val alarms = listOf(
            Alarm(
                id = 1,
                time = LocalTime.of(7, 30),
                label = "Утренний",
                isEnabled = true,
                repeatDays = setOf(DayOfWeek.MONDAY, DayOfWeek.TUESDAY)
            ),
            Alarm(
                id = 2,
                time = LocalTime.of(8, 0),
                label = "Работа",
                isEnabled = false,
                repeatDays = emptySet()
            )
        )

        composeTestRule.activity.setContent {
            AlarmTheme {
                AlarmListScreenContent(
                    state = AlarmListState(isLoading = false, alarms = alarms),
                    onNavigateToEdit = {},
                    toggleAlarm = {},
                    deleteAlarm = {}
                )
            }
        }

        composeTestRule.onNodeWithText("07:30").assertIsDisplayed()
        composeTestRule.onNodeWithText("08:00").assertIsDisplayed()
        composeTestRule.onNodeWithText("Утренний").assertIsDisplayed()
        composeTestRule.onNodeWithText("Работа").assertIsDisplayed()
    }

    @Test
    fun alarmListScreen_toggleSwitch_callsCallback() {
        var toggleCalled = false
        val alarm = Alarm(
            id = 1,
            time = LocalTime.of(7, 30),
            isEnabled = true
        )

        composeTestRule.activity.setContent {
            AlarmTheme {
                AlarmListScreenContent(
                    state = AlarmListState(isLoading = false, alarms = listOf(alarm)),
                    onNavigateToEdit = {},
                    toggleAlarm = { toggleCalled = true },
                    deleteAlarm = {}
                )
            }
        }

        composeTestRule.onNodeWithText("07:30")
            .performClick()

        composeTestRule.waitForIdle()

        Assert.assertEquals(true, toggleCalled)
    }

    @Test
    fun alarmListScreen_fabNavigatesToCreate() {
        var navigatedToEdit = -1L

        composeTestRule.activity.setContent {
            AlarmTheme {
                AlarmListScreenContent(
                    state = AlarmListState(isLoading = false),
                    onNavigateToEdit = { navigatedToEdit = it },
                    toggleAlarm = {},
                    deleteAlarm = {}
                )
            }
        }

        composeTestRule.onNodeWithContentDescription("Добавить будильник")
            .performClick()

        composeTestRule.waitForIdle()

        Assert.assertEquals(-1L, navigatedToEdit)
    }

    @Test
    fun alarmListScreen_deleteAlarm_callsCallback() {
        var deletedAlarmId = -1L
        val alarm = Alarm(
            id = 42,
            time = LocalTime.of(7, 30),
            label = "Test"
        )

        composeTestRule.activity.setContent {
            AlarmTheme {
                AlarmListScreenContent(
                    state = AlarmListState(isLoading = false, alarms = listOf(alarm)),
                    onNavigateToEdit = {},
                    toggleAlarm = {},
                    deleteAlarm = { deletedAlarmId = it.id }
                )
            }
        }

        composeTestRule.onNodeWithText("07:30")
            .performTouchInput { swipeLeft() }

        composeTestRule.waitForIdle()

        Assert.assertEquals(42L, deletedAlarmId)
    }

    @Test
    fun alarmListScreen_showsOneTimeLabel_whenNoRepeatDays() {
        val alarm = Alarm(
            id = 1,
            time = LocalTime.of(7, 30),
            repeatDays = emptySet()
        )

        composeTestRule.activity.setContent {
            AlarmTheme {
                AlarmListScreenContent(
                    state = AlarmListState(isLoading = false, alarms = listOf(alarm)),
                    onNavigateToEdit = {},
                    toggleAlarm = {},
                    deleteAlarm = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Однократно").assertIsDisplayed()
    }
}
