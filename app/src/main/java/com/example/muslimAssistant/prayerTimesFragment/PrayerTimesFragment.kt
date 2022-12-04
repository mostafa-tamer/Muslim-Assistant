package com.example.muslimAssistant.prayerTimesFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.Fragment
import com.example.muslimAssistant.Timing
import com.example.muslimAssistant.databinding.FragmentPrayerTimesBinding
import com.example.muslimAssistant.notifications.AlarmGenerator
import com.example.muslimAssistant.notifications.ChannelHelper
import com.example.muslimAssistant.notifications.ChannelIDs
import org.koin.android.ext.android.get
import org.koin.androidx.viewmodel.ext.android.viewModel


class PrayerTimesFragment : Fragment() {

    private lateinit var binding: FragmentPrayerTimesBinding

    private val prayerTimesViewModel: PrayerTimesViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentPrayerTimesBinding.inflate(layoutInflater)

        binding.lifecycleOwner = this

        binding.homeViewModel = prayerTimesViewModel

        updateViewModelPrayerTimes()

        cancelNotifications()

        val alarmGenerator = get<AlarmGenerator>()

        alarmGenerator.setPrayerTimesAlarms()

        channelCreator()

        return binding.root
    }

    private fun cancelNotifications() {
        NotificationManagerCompat.from(requireContext()).cancelAll()
    }

    private fun channelCreator() {
        ChannelHelper(
            requireNotNull(activity).applicationContext,
            ChannelIDs.PRIORITY_MAX.ID,
            "defaultChannel"
        )
    }

    private fun updateViewModelPrayerTimes() {

        prayerTimesViewModel.prayerTimesList.observe(viewLifecycleOwner) {
            for (i in it) {
                if (i.date == Timing.todayDate) {
                    prayerTimesViewModel.date.value = i.date
                    prayerTimesViewModel.fajr.value = i.fajr.substring(0, 5)
                    prayerTimesViewModel.sunrise.value = i.sunrise.substring(0, 5)
                    prayerTimesViewModel.dhuhr.value = i.dhuhr.substring(0, 5)
                    prayerTimesViewModel.asr.value = i.asr.substring(0, 5)
                    prayerTimesViewModel.maghrib.value = i.maghrib.substring(0, 5)
                    prayerTimesViewModel.isha.value = i.isha.substring(0, 5)
                }
            }
        }
    }
}