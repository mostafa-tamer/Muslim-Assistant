package com.example.muslimAssistant.fragments.mainFragment

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.muslimAssistant.fragments.azkarFragment.AzkarFragment
import com.example.muslimAssistant.fragments.prayerTimesFragment.PrayerTimesFragment

class ViewPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        if (position == 1)
            return AzkarFragment()

        return PrayerTimesFragment()
    }
}