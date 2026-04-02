package com.kurban.alarm.presentation.alarm

import android.app.KeyguardManager
import android.content.Context
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.kurban.alarm.domain.repository.AlarmRepository
import com.kurban.alarm.notification.AlarmConstants
import com.kurban.alarm.notification.AlarmScheduler
import com.kurban.alarm.presentation.theme.AlarmTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class AlarmRingActivity : ComponentActivity() {

    @Inject
    lateinit var alarmScheduler: AlarmScheduler

    @Inject
    lateinit var alarmRepository: AlarmRepository

    private var mediaPlayer: MediaPlayer? = null
    private var vibrator: Vibrator? = null
    private var alarmId: Long = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupWindow()

        alarmId = intent.getLongExtra(AlarmScheduler.EXTRA_ALARM_ID, -1)
        val label = intent.getStringExtra(AlarmScheduler.EXTRA_ALARM_LABEL) ?: ""
        val isSnooze = intent.getBooleanExtra(AlarmScheduler.EXTRA_ALARM_IS_SNOOZE, false)
        val vibrate = intent.getBooleanExtra(AlarmScheduler.EXTRA_ALARM_VIBRATE, true)

        startAlarmSound()
        if (vibrate) startVibration()

        setContent {
            AlarmTheme {
                AlarmRingScreen(
                    label = label,
                    onDismiss = {
                        handleDismiss()
                    },
                    onSnooze = {
                        handleSnooze()
                    }
                )
            }
        }
    }

    private fun handleDismiss() {
        stopAlarm()
        if (alarmId != -1L) {
            CoroutineScope(Dispatchers.IO).launch {
                val alarm = alarmRepository.getAlarmById(alarmId)
                alarm?.let {
                    if (it.isEnabled && it.isRepeating) {
                        alarmScheduler.reschedule(it)
                    } else if (it.isEnabled) {
                        alarmScheduler.cancel(it)
                    }
                }
            }
        }
        finish()
    }

    private fun handleSnooze() {
        stopAlarm()
        if (alarmId != -1L) {
            CoroutineScope(Dispatchers.IO).launch {
                val alarm = alarmRepository.getAlarmById(alarmId)
                alarm?.let {
                    alarmScheduler.snooze(it, AlarmConstants.SNOOZE_MINUTES)
                }
            }
        }
        finish()
    }

    private fun setupWindow() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
            val keyguardManager = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
            keyguardManager.requestDismissKeyguard(this, null)
        } else {
            @Suppress("DEPRECATION")
            window.addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON or
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
            )
        }

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    private fun startAlarmSound() {
        try {
            val alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
                ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            mediaPlayer = MediaPlayer().apply {
                setDataSource(this@AlarmRingActivity, alarmUri)
                isLooping = true
                prepare()
                start()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun startVibration() {
        vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }

        vibrator?.vibrate(VibrationEffect.createWaveform(AlarmConstants.VIBRATION_PATTERN, 0))
    }

    private fun stopAlarm() {
        mediaPlayer?.apply {
            if (isPlaying) stop()
            release()
        }
        mediaPlayer = null
        vibrator?.cancel()
        vibrator = null
    }

    override fun onDestroy() {
        super.onDestroy()
        stopAlarm()
    }
}
