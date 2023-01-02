package com.example.muslimAssistant.fragments.mainFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.example.muslimAssistant.databinding.FragmentMainBinding
import com.google.android.material.tabs.TabLayout


class MainFragment : Fragment() {

    lateinit var binding: FragmentMainBinding

    lateinit var tabLayout: TabLayout
    lateinit var viewPager2: ViewPager2
    lateinit var viewPagerAdapter: ViewPagerAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentMainBinding.inflate(layoutInflater)


        tabLayout = binding.tabLayout

        viewPager2 = binding.viewPager2

        viewPagerAdapter = ViewPagerAdapter(this)
        viewPager2.adapter = viewPagerAdapter
          
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {

                viewPager2.currentItem = tab.position
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}

            override fun onTabReselected(tab: TabLayout.Tab) {}
        })


        viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                tabLayout.getTabAt(position)?.select()
            }
        })

        return binding.root
    }

}