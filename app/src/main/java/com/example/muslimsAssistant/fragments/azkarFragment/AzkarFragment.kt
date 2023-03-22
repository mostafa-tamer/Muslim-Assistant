package com.example.muslimsAssistant.fragments.azkarFragment

import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import com.example.muslimsAssistant.Timing.convertDateTimeNoSecToMillisNoSec
import com.example.muslimsAssistant.Timing.getTodayDate
import com.example.muslimsAssistant.databinding.FragmentAzkarBinding
import com.example.muslimsAssistant.notifications.AlarmManagerHelper
import kotlinx.coroutines.runBlocking

class AzkarFragment : Fragment() {

    private lateinit var binding: FragmentAzkarBinding
    val isDay = MutableLiveData(true)
    private var pageCounter = 0
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentAzkarBinding.inflate(layoutInflater)

//        handleMenu()


        detectAzkar()

        isDayObserver()
        handleZoom()
        controller()

        return binding.root
    }

    private fun detectAzkar() {

        runBlocking {

            val alarmHelper = AlarmManagerHelper(requireContext())
            val prayerTimes = alarmHelper.retDayPrayerTimes(getTodayDate())

            val fajr = prayerTimes.fajr
            val asr = prayerTimes.asr

            val fajrMillis = convertDateTimeNoSecToMillisNoSec("${getTodayDate()} $fajr")
            val asrMillis = convertDateTimeNoSecToMillisNoSec("${getTodayDate()} $asr")
            val currentMillis = System.currentTimeMillis()

            if (currentMillis in fajrMillis until asrMillis) {
                isDay.value = true
                binding.azkarText.text = "أذكار الصباح"
            } else {
                isDay.value = false
                binding.azkarText.text = "أذكار المساء"
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

//    private fun handleMenu() {
//        requireActivity().addMenuProvider(
//            object : MenuProvider {
//                override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
//                    menuInflater.inflate(R.menu.menu_azkar, menu)
//                }
//
//                override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
//                    when (menuItem.itemId) {
//                        R.id.sabah -> {
//                            isDay.value = true
//                        }
//                        R.id.masaa -> {
//                            isDay.value = false
//                        }
//                        else -> return false
//                    }
//                    return true
//                }
//            }, viewLifecycleOwner, Lifecycle.State.RESUMED
//        )
//    }

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