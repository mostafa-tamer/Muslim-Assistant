package com.example.muslimAssistant

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.muslimAssistant.azkarFragment.AzkarFragment
import com.example.muslimAssistant.databinding.ActivityMainBinding
import com.example.muslimAssistant.prayerTimesFragment.PrayerTimesFragment


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val prayerTimesFragment = PrayerTimesFragment()
    private val azkarFragment = AzkarFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        replaceFragments(prayerTimesFragment)

        binding.prayerTimes.setOnClickListener {

            replaceFragments(prayerTimesFragment)
        }
        binding.azkar.setOnClickListener {

            replaceFragments(azkarFragment)
        }
    }

    private fun replaceFragments(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.mainFrameLayout, fragment).commit()
    }
}