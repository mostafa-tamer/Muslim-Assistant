package com.android.muslimAssistant.activities

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationManagerCompat
import com.android.muslimAssistant.ChannelIDs
import com.android.muslimAssistant.R
import com.android.muslimAssistant.databinding.ActivityMainBinding
import com.android.muslimAssistant.notifications.ChannelHelper
import com.android.muslimAssistant.repository.SharedPreferencesRepository
import com.android.muslimAssistant.widgets.PrayerTimesWidget
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.koin.java.KoinJavaComponent.get
import java.util.*


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        run()
    }

    override fun onStop() {
        super.onStop()
        finish()
    }

    private fun run() {
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        handelLanguage()
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

    private fun handelLanguage() {
        val repository = get<SharedPreferencesRepository>(SharedPreferencesRepository::class.java)
        runBlocking {
            val language: String = repository.language.first()
            val locale = Locale(language)
            val resources = resources
            val configuration = resources.configuration
            configuration.setLocale(locale)
            val updatedResources = Resources(assets, resources.displayMetrics, configuration)
            resources.updateConfiguration(configuration, updatedResources.displayMetrics)
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