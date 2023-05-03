package com.android.muslimAssistant.fragments.mainFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainBinding.inflate(layoutInflater)
        methodAlertDialog = AlertDialogWrapper.Builder(requireContext())

        handleViewPager()
        return binding.root
    }

    private fun resetColors(imageButton: ImageButton) {
        imageButton.setBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.blue
            )
        )
    }

    private fun coloring(imageButton: ImageButton) {
        resetColors(binding.tasbeehButton)
        resetColors(binding.reminderButton)
        resetColors(binding.prayerTimesButton)
        resetColors(binding.azkarButton)

        imageButton.setBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.light_blue
            )
        )
    }

    override fun onResume() {
        super.onResume()
        selectButtonToColor(viewPager2.currentItem)
    }

    fun selectButtonToColor(position: Int) {
        when (position) {
            0 -> {
                coloring(binding.azkarButton)
            }
            1 -> {
                coloring(binding.prayerTimesButton)
            }
            2 -> {
                coloring(binding.reminderButton)
            }
            else -> {
                coloring(binding.tasbeehButton)
            }
        }
    }

    private fun handleViewPager() {
        viewPager2 = binding.viewPager2
        viewPager2.adapter = ViewPagerAdapter(this)
        viewPager2.setCurrentItem(1, false)

        viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                selectButtonToColor(position)
            }
        })

        val listOfButtons = mutableListOf(
            binding.azkarButton,
            binding.prayerTimesButton,
            binding.reminderButton,
            binding.tasbeehButton
        )

        for (i in listOfButtons) {
            i.setOnClickListener {
                coloring(it as ImageButton)
                viewPager2.currentItem = listOfButtons.indexOf(i)
            }
        }

//        viewPager2.isUserInputEnabled = false
//        val fadeInAnimation = AnimationUtils.loadAnimation(requireContext(), R.anim.fade_in)
//        viewPager2.setPageTransformer { page, position ->
//            page.startAnimation(fadeInAnimation)
//        }

    }
}