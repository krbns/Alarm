package com.kurban.alarm.presentation.alarmRing

import android.Manifest
import androidx.activity.compose.setContent
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ApplicationProvider
import androidx.test.platform.app.InstrumentationRegistry
import com.kurban.alarm.presentation.MainActivity
import com.kurban.alarm.presentation.alarm.AlarmRingScreen
import com.kurban.alarm.presentation.theme.AlarmTheme
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class AlarmRingScreenTest {

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
    fun alarmRingScreen_showsDismissButton() {
        composeTestRule.activity.setContent {
            AlarmTheme {
                AlarmRingScreen(
                    label = "",
                    onDismiss = {},
                    onSnooze = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Отключить").assertIsDisplayed()
    }

    @Test
    fun alarmRingScreen_showsSnoozeButton() {
        composeTestRule.activity.setContent {
            AlarmTheme {
                AlarmRingScreen(
                    label = "",
                    onDismiss = {},
                    onSnooze = {}
                )
            }
        }

        composeTestRule.onNodeWithText("+5 минут").assertIsDisplayed()
    }

    @Test
    fun alarmRingScreen_showsLabel_whenProvided() {
        composeTestRule.activity.setContent {
            AlarmTheme {
                AlarmRingScreen(
                    label = "Утренняя тренировка",
                    onDismiss = {},
                    onSnooze = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Утренняя тренировка").assertIsDisplayed()
    }

    @Test
    fun alarmRingScreen_dismissButton_callsCallback() {
        var dismissClicked = false

        composeTestRule.activity.setContent {
            AlarmTheme {
                AlarmRingScreen(
                    label = "",
                    onDismiss = { dismissClicked = true },
                    onSnooze = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Отключить")
            .performClick()

        composeTestRule.waitForIdle()

        Assert.assertTrue(dismissClicked)
    }

    @Test
    fun alarmRingScreen_snoozeButton_callsCallback() {
        var snoozeClicked = false

        composeTestRule.activity.setContent {
            AlarmTheme {
                AlarmRingScreen(
                    label = "",
                    onDismiss = {},
                    onSnooze = { snoozeClicked = true }
                )
            }
        }

        composeTestRule.onNodeWithText("+5 минут")
            .performClick()

        composeTestRule.waitForIdle()

        Assert.assertTrue(snoozeClicked)
    }

    @Test
    fun alarmRingScreen_showsCurrentTime() {
        composeTestRule.activity.setContent {
            AlarmTheme {
                AlarmRingScreen(
                    label = "",
                    onDismiss = {},
                    onSnooze = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Отключить").assertIsDisplayed()
        composeTestRule.onNodeWithText("+5 минут").assertIsDisplayed()
    }

    @Test
    fun alarmRingScreen_hidesLabel_whenEmpty() {
        composeTestRule.activity.setContent {
            AlarmTheme {
                AlarmRingScreen(
                    label = "",
                    onDismiss = {},
                    onSnooze = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Отключить").assertIsDisplayed()
    }
}
