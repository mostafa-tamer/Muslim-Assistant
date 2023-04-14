package com.example.muslimsAssistant.fragments.mainFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.example.muslimsAssistant.databinding.FragmentMainBinding
import com.google.android.material.tabs.TabLayout


class MainFragment : Fragment() {

    lateinit var binding: FragmentMainBinding

    lateinit var viewPager2: ViewPager2

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentMainBinding.inflate(layoutInflater)


        viewPager2 = binding.viewPager2

        viewPager2.adapter = ViewPagerAdapter(this)

        selectPrayerTimesFragment()

        binding.tasbeeh.setOnClickListener {
            viewPager2.currentItem = 0
        }
        binding.reminderButton.setOnClickListener {
            viewPager2.currentItem = 1
        }
        binding.prayerTimesButton.setOnClickListener {
            viewPager2.currentItem = 2
        }
        binding.azkarButton.setOnClickListener {
            viewPager2.currentItem = 3
        }
        return binding.root
    }

    private fun selectPrayerTimesFragment() {
        viewPager2.setCurrentItem(2, false)
    }
}