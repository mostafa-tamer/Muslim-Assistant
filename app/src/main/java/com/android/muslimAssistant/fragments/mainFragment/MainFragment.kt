package com.android.muslimAssistant.fragments.mainFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.android.muslimAssistant.R
import com.android.muslimAssistant.databinding.FragmentMainBinding
import com.android.muslimAssistant.utils.AlertDialogWrapper
import org.koin.androidx.viewmodel.ext.android.viewModel


class MainFragment : Fragment() {

    private lateinit var binding: FragmentMainBinding
    private lateinit var viewPager2: ViewPager2
    private lateinit var methodAlertDialog: AlertDialogWrapper.Builder
    private val viewModel: MainFragmentViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainBinding.inflate(layoutInflater)
        methodAlertDialog = AlertDialogWrapper.Builder(requireContext())

        handleViewPager()
        return binding.root
    }

    private fun handleViewPager() {
        viewPager2 = binding.viewPager2
        viewPager2.adapter = ViewPagerAdapter(this)
        viewPager2.setCurrentItem(1, false)
        viewPager2.isUserInputEnabled = false

        binding.prayerTimesButton.setBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.light_blue
            )
        )

        binding.tasbeeh.setOnClickListener {
            viewPager2.setCurrentItem(3, false)
            coloring()
            binding.tasbeeh.setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.light_blue
                )
            )
        }
        binding.reminderButton.setOnClickListener {
            viewPager2.setCurrentItem(2, false)
            coloring()
            binding.reminderButton.setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.light_blue
                )
            )
        }
        binding.prayerTimesButton.setOnClickListener {
            viewPager2.setCurrentItem(1, false)
            coloring()
            binding.prayerTimesButton.setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.light_blue
                )
            )
        }
        binding.azkarButton.setOnClickListener {
            viewPager2.setCurrentItem(0, false)
            coloring()
            binding.azkarButton.setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.light_blue
                )
            )
        }
    }

    private fun coloring() {
        binding.tasbeeh.setBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.blue
            )
        )
        binding.azkarButton.setBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.blue
            )
        )
        binding.prayerTimesButton.setBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.blue
            )
        )
        binding.reminderButton.setBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.blue
            )
        )
    }
}