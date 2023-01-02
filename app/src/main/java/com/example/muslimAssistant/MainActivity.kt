package com.example.muslimAssistant

import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.example.muslimAssistant.fragments.azkarFragment.AzkarFragment
import com.example.muslimAssistant.databinding.ActivityMainBinding
import com.example.muslimAssistant.fragments.prayerTimesFragment.PrayerTimesFragment


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        drawerLayout = binding.drawerLayout

        drawerLayout = binding.drawerLayout
        val navController = findNavController(R.id.fragmentContainerView)
        NavigationUI.setupActionBarWithNavController(this, navController, drawerLayout)

        appBarConfiguration = AppBarConfiguration(navController.graph, drawerLayout)
        NavigationUI.setupWithNavController(binding.navView, navController)

        //change action bar direction
        //window.decorView.layoutDirection = View.LAYOUT_DIRECTION_RTL;

    }


    override fun onSupportNavigateUp(): Boolean {
        val navController = this.findNavController(R.id.fragmentContainerView)
        return NavigationUI.navigateUp(navController, drawerLayout)

    }

}