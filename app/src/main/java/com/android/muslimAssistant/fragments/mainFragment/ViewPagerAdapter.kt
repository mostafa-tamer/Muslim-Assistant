package com.android.muslimAssistant.fragments.mainFragment

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.android.muslimAssistant.fragments.azkarFragment.AzkarFragment
import com.android.muslimAssistant.fragments.prayerTimesFragment.PrayerTimesFragment
import com.android.muslimAssistant.fragments.reminderFragment.ReminderFragment
import com.android.muslimAssistant.fragments.tasbeehFragment.TasbeehFragment

class ViewPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    override fun getItemCount() = 4

    private val azkarFragment = AzkarFragment()
    private val prayerTimesFragment = PrayerTimesFragment()
    private val reminderFragment = ReminderFragment()
    private val tasbeehFragment = TasbeehFragment()

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> return azkarFragment
            1 -> return prayerTimesFragment
            2 -> return reminderFragment
            else -> tasbeehFragment
        }
    }
}