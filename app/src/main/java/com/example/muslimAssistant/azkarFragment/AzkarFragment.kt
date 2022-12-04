package com.example.muslimAssistant.azkarFragment

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import com.example.muslimAssistant.PrayerTimesMatcher
import com.example.muslimAssistant.R
import com.example.muslimAssistant.Timing
import com.example.muslimAssistant.databinding.FragmentAzkarBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AzkarFragment : Fragment() {

    private lateinit var binding: FragmentAzkarBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAzkarBinding.inflate(layoutInflater)

        setHasOptionsMenu(true)

        autoSelectAzkar()

        return binding.root
    }

    private fun autoSelectAzkar() {
        CoroutineScope(Dispatchers.Default).launch {
            val prayerTimesMatcher = PrayerTimesMatcher()
            val afterFajrAndBeforeAsr = System.currentTimeMillis() >
                    Timing.convertDateTimeToMillis(
                        Timing.todayDate + " " +
                                prayerTimesMatcher.retrievePrayerTimes(
                                    Timing.todayDate
                                ).fajr
                    ) && System.currentTimeMillis() <
                    Timing.convertDateTimeToMillis(
                        Timing.todayDate + " " +
                                prayerTimesMatcher.retrievePrayerTimes(
                                    Timing.todayDate
                                ).asr
                    )

            val afterAsr = System.currentTimeMillis() >
                    Timing.convertDateTimeToMillis(
                        Timing.todayDate + " " +
                                prayerTimesMatcher.retrievePrayerTimes(
                                    Timing.todayDate
                                ).asr
                    )

            if (afterFajrAndBeforeAsr) {
                binding.azkar.text = resources.getText(R.string.azkarAlSabah)
            } else if (afterAsr) {
                binding.azkar.text = resources.getText(R.string.azkarAlMasaa)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_azkar, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.sabah -> {
                binding.azkar.text = resources.getText(R.string.azkarAlSabah)
            }
            R.id.masaa -> {
                binding.azkar.text = resources.getText(R.string.azkarAlMasaa)
            }
        }
        return super.onOptionsItemSelected(item)
    }

}