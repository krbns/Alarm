package com.kurban.alarm.presentation.navigation

sealed class Screen(val route: String) {
    data object AlarmList : Screen("alarm_list")
    data object EditAlarm : Screen("edit_alarm/{alarmId}") {
        fun createRoute(alarmId: Long = -1L) = "edit_alarm/$alarmId"
    }
}
