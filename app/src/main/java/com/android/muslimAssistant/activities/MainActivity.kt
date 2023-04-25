package com.android.muslimAssistant.activities


import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationManagerCompat
import com.android.muslimAssistant.ChannelIDs
import com.android.muslimAssistant.R
import com.android.muslimAssistant.databinding.ActivityMainBinding
import com.android.muslimAssistant.notifications.ChannelHelper
import com.android.muslimAssistant.repository.SharedPreferencesRepository
import com.android.muslimAssistant.utils.updateLanguage
import com.android.muslimAssistant.widgets.PrayerTimesWidget
import org.koin.android.ext.android.get


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)

        run()
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
        channelCreator()
        updateWidget()
    }

    private fun handleToolBar() {
        binding.toolbar.settingButton.visibility = View.VISIBLE
        binding.toolbar.settingButton.setOnClickListener {
            startActivity(Intent(this, ConfigurationActivity::class.java))
            finish()
        }
    }

    private fun updateWidget() {
        val appWidgetManager = AppWidgetManager.getInstance(applicationContext)
        val componentName = ComponentName(applicationContext, PrayerTimesWidget::class.java)
        val appWidgetIds = appWidgetManager.getAppWidgetIds(componentName)
        val widget = PrayerTimesWidget()
        widget.onUpdate(applicationContext, appWidgetManager, appWidgetIds)
    }

    private fun channelCreator() {
        ChannelHelper(
            applicationContext,
            ChannelIDs.PRIORITY_MAX.ID,
            "defaultChannel"
        )
    }

    private fun cancelNotifications() {
        NotificationManagerCompat.from(this).cancelAll()
    }
}