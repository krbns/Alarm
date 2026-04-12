package com.kurban.alarm.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.kurban.alarm.domain.model.Alarm
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AlarmScheduler @Inject constructor(
    @ApplicationContext private val context: Context,
    private val alarmManager: AlarmManager
) {
    fun schedule(alarm: Alarm) {
        if (!alarm.isEnabled) return

        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra(EXTRA_ALARM_ID, alarm.id)
            putExtra(EXTRA_ALARM_LABEL, alarm.label)
            putExtra(EXTRA_ALARM_VIBRATE, alarm.isVibrate)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            alarm.id.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val triggerTime = alarm.getNextTriggerTime()
        Log.d(TAG, "Scheduling alarm ${alarm.id} at $triggerTime (${triggerTime - System.currentTimeMillis()}ms from now)")

        val scheduled = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (alarmManager.canScheduleExactAlarms()) {
                alarmManager.setAlarmClock(
                    AlarmManager.AlarmClockInfo(triggerTime, pendingIntent),
                    pendingIntent
                )
                true
            } else {
                Log.w(TAG, "Exact alarm permission not granted, using fallback")
                scheduleFallback(triggerTime, pendingIntent)
            }
        } else {
            alarmManager.setAlarmClock(
                AlarmManager.AlarmClockInfo(triggerTime, pendingIntent),
                pendingIntent
            )
            true
        }

        if (scheduled) {
            Log.d(TAG, "Alarm ${alarm.id} scheduled successfully")
        } else {
            Log.e(TAG, "Failed to schedule alarm ${alarm.id}")
        }
    }

    private fun scheduleFallback(triggerTime: Long, pendingIntent: PendingIntent): Boolean {
        return try {
            alarmManager.setAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerTime,
                pendingIntent
            )
            true
        } catch (e: Exception) {
            Log.e(TAG, "Fallback scheduling failed", e)
            false
        }
    }

    fun cancel(alarm: Alarm) {
        val intent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            alarm.id.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
        Log.d(TAG, "Alarm ${alarm.id} cancelled")
    }

    fun reschedule(alarm: Alarm) {
        cancel(alarm)
        schedule(alarm)
    }

    fun snooze(alarm: Alarm, minutes: Int = 5): Long {
        val snoozeTime = System.currentTimeMillis() + minutes * 60 * 1000L
        Log.d(TAG, "Snoozing alarm ${alarm.id} for $minutes minutes, will fire at $snoozeTime")

        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra(EXTRA_ALARM_ID, alarm.id)
            putExtra(EXTRA_ALARM_LABEL, alarm.label)
            putExtra(EXTRA_ALARM_VIBRATE, alarm.isVibrate)
            putExtra(EXTRA_ALARM_IS_SNOOZE, true)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            (alarm.id + SNOOZE_REQUEST_CODE_OFFSET).toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (alarmManager.canScheduleExactAlarms()) {
                alarmManager.setAlarmClock(
                    AlarmManager.AlarmClockInfo(snoozeTime, pendingIntent),
                    pendingIntent
                )
            } else {
                alarmManager.setAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    snoozeTime,
                    pendingIntent
                )
            }
        } else {
            alarmManager.setAlarmClock(
                AlarmManager.AlarmClockInfo(snoozeTime, pendingIntent),
                pendingIntent
            )
        }

        return snoozeTime
    }

    fun cancelSnooze(alarm: Alarm) {
        val intent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            (alarm.id + SNOOZE_REQUEST_CODE_OFFSET).toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }

    fun canScheduleExactAlarms(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            alarmManager.canScheduleExactAlarms()
        } else {
            true
        }
    }

    companion object {
        private const val TAG = "AlarmScheduler"
        const val EXTRA_ALARM_ID = "extra_alarm_id"
        const val EXTRA_ALARM_LABEL = "extra_alarm_label"
        const val EXTRA_ALARM_VIBRATE = "extra_alarm_vibrate"
        const val EXTRA_ALARM_IS_SNOOZE = "extra_alarm_is_snooze"
        private const val SNOOZE_REQUEST_CODE_OFFSET = 10000
    }
}
