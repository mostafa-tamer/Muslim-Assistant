package com.example.muslimsAssistant

import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationManagerCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.ui.AppBarConfiguration
import com.example.muslimsAssistant.databinding.ActivityMainBinding
import com.example.muslimsAssistant.notifications.ChannelHelper


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        cancelNotifications()
        channelCreator()




//        drawerLayoutSetup()
    }

    private fun drawerLayoutSetup() {
//        drawerLayout = binding.drawerLayout
//        val navController = findNavController(R.id.fragmentContainerView)
//        NavigationUI.setupActionBarWithNavController(this, navController, drawerLayout)
//
//        appBarConfiguration = AppBarConfiguration(navController.graph, drawerLayout)
//        NavigationUI.setupWithNavController(binding.navView, navController)
    }

    private fun channelCreator() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ChannelHelper(
                applicationContext,
                ChannelIDs.PRIORITY_MAX.ID,
                "defaultChannel"
            )
        }
    }

    private fun cancelNotifications() {
        NotificationManagerCompat.from(this).cancelAll()
    }

//    override fun onSupportNavigateUp(): Boolean {
//        val navController = this.findNavController(R.id.fragmentContainerView)
//        return NavigationUI.navigateUp(navController, drawerLayout)
//    }
}