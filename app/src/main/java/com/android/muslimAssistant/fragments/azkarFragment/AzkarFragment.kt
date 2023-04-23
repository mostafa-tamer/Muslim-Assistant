package com.android.muslimAssistant.fragments.azkarFragment

import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import com.android.muslimAssistant.Timing
import com.android.muslimAssistant.R
import com.android.muslimAssistant.databinding.FragmentAzkarBinding
import com.android.muslimAssistant.notifications.AlarmHandler
import com.android.muslimAssistant.repository.PrayerTimesRepository
import kotlinx.coroutines.runBlocking
import org.koin.java.KoinJavaComponent.get

class AzkarFragment : Fragment() {
    private val timing by lazy { Timing() }

    private lateinit var binding: FragmentAzkarBinding
    val isDay = MutableLiveData(true)
    private var pageCounter = 0
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentAzkarBinding.inflate(layoutInflater)

        detectAzkar()

        isDayObserver()
        handleZoom()
        controller()

        return binding.root
    }

    private fun detectAzkar() {

        runBlocking {

            val prayerTimes =
                get<PrayerTimesRepository>(PrayerTimesRepository::class.java).retDayPrayerTimes(
                    timing.getTodayDate()
                )

            val fajr = prayerTimes.fajr
            val asr = prayerTimes.asr

            val fajrMillis = timing.convertDmyHmToMillis("${timing.getTodayDate()} $fajr")
            val asrMillis = timing.convertDmyHmToMillis("${timing.getTodayDate()} $asr")
            val currentMillis = System.currentTimeMillis()

            if (currentMillis in fajrMillis until asrMillis) {
                isDay.value = true
                binding.azkarText.text = getString(R.string.morningRemembrance)
            } else {
                isDay.value = false
                binding.azkarText.text = getString(R.string.eveningRemembrance)
            }
        }
    }

    private fun isDayObserver() {
        isDay.observe(viewLifecycleOwner) {
            if (it) {
                binding.zikr.text = dayAzkarList[pageCounter]
            } else {
                binding.zikr.text = nightAzkarList[pageCounter]
            }
        }
    }

    private fun handleZoom() {
        binding.plus.setOnClickListener {
            if (binding.zikr.textSize < 112) {
                binding.zikr.setTextSize(TypedValue.COMPLEX_UNIT_PX, binding.zikr.textSize + 8)
            }
        }
        binding.minus.setOnClickListener {
            if (binding.zikr.textSize > 48) {
                binding.zikr.setTextSize(TypedValue.COMPLEX_UNIT_PX, binding.zikr.textSize - 8)
            }
        }
    }

    private fun controller() {
        binding.next.setOnClickListener {
            if (isDay.value!!) {
                if (pageCounter + 1 < dayAzkarList.size) pageCounter++
                binding.zikr.text = dayAzkarList[pageCounter]
            } else {
                if (pageCounter + 1 < nightAzkarList.size) pageCounter++
                binding.zikr.text = nightAzkarList[pageCounter]
            }
            updateIterator(pageCounter)
        }

        binding.previous.setOnClickListener {
            if (isDay.value!!) {
                if (pageCounter - 1 >= 0) pageCounter--
                binding.zikr.text = dayAzkarList[pageCounter]
            } else {
                if (pageCounter - 1 >= 0) pageCounter--
                binding.zikr.text = nightAzkarList[pageCounter]
            }
            updateIterator(pageCounter)
        }
    }

    private fun updateIterator(counter: Int) {
        val iteratorText = (counter + 1).toString() + " / " + dayAzkarList.size
        binding.iterator.text = iteratorText
    }
}