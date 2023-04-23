package com.android.muslimAssistant.fragments.mainFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
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
        println("main fragment")

        binding = FragmentMainBinding.inflate(layoutInflater)
        methodAlertDialog = AlertDialogWrapper.Builder(requireContext())



        handleViewPager()
        return binding.root
    }

    private fun handleViewPager() {
        viewPager2 = binding.viewPager2
        viewPager2.adapter = ViewPagerAdapter(this)
        viewPager2.setCurrentItem(1, false)

        binding.tasbeeh.setOnClickListener {
            viewPager2.currentItem = 3
        }
        binding.reminderButton.setOnClickListener {
            viewPager2.currentItem = 2
        }
        binding.prayerTimesButton.setOnClickListener {
            viewPager2.currentItem = 1
        }
        binding.azkarButton.setOnClickListener {
            viewPager2.currentItem = 0
        }
    }
}