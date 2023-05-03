package com.android.muslimAssistant.activities


import android.app.NotificationManager
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import com.android.muslimAssistant.ChannelIDs
import com.android.muslimAssistant.R
import com.android.muslimAssistant.databinding.ActivityMainBinding
import com.android.muslimAssistant.notifications.AlarmHandler
import com.android.muslimAssistant.notifications.ChannelHelper
import com.android.muslimAssistant.receivers.PrayerTimesReceiver
import com.android.muslimAssistant.utils.startNewService
import com.android.muslimAssistant.utils.updateLanguage


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding


    private val notificationId = 1
    private var notificationText = "Initial message"
    private lateinit var notificationManager: NotificationManager
    private lateinit var notificationBuilder: NotificationCompat.Builder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)

        run()
        startNewService(this)
    }

    override fun onRestart() {
        super.onRestart()
        updateLanguage(this)
    }

    private fun run() {
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        updateLanguage(this)
        handleToolBar()
        prayerTimesNotificationChannel()
        lowPriorityNotificationChannel()
    }

    private fun lowPriorityNotificationChannel() {
        ChannelHelper(
            applicationContext,
            ChannelIDs.PRIORITY_MIN.ID,
            "lowPriorityNotificationChannel",
            NotificationManager.IMPORTANCE_MIN
        )
    }

    private fun handleToolBar() {
        binding.toolbar.settingButton.visibility = View.VISIBLE
        binding.toolbar.settingButton.setOnClickListener {
            startActivity(Intent(this, ConfigurationActivity::class.java))
            finish()
        }
    }



    private fun prayerTimesNotificationChannel() {
        ChannelHelper(
            applicationContext,
            ChannelIDs.PRIORITY_MAX.ID,
            "prayerTimesNotificationChannel",
            NotificationManager.IMPORTANCE_HIGH
        )
    }
}


//AlarmHandler(this).generateAlarmExactTime(
//this,
//System.currentTimeMillis(),
//"2",
//213904871,
//PrayerTimesReceiver::class.java,
//)