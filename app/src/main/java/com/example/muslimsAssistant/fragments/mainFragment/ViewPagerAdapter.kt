package com.example.muslimsAssistant.fragments.mainFragment

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.muslimsAssistant.fragments.azkarFragment.AzkarFragment
import com.example.muslimsAssistant.fragments.prayerTimesFragment.PrayerTimesFragment
import com.example.muslimsAssistant.fragments.reminderFragment.ReminderFragment
import com.example.muslimsAssistant.fragments.tasbeehFragment.TasbeehFragment

class ViewPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    override fun getItemCount() = 4

    override fun createFragment(position: Int): Fragment {
        when (position) {
            0 -> return TasbeehFragment()
            1 -> return ReminderFragment()
            2 -> return PrayerTimesFragment()
        }
        return AzkarFragment()
    }
}