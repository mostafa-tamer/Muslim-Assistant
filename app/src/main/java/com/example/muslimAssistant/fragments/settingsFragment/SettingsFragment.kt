package com.example.muslimAssistant.fragments.settingsFragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.muslimAssistant.R
import com.example.muslimAssistant.databinding.FragmentSettingsBinding

class SettingFragment : Fragment() {

    lateinit var binding:FragmentSettingsBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentSettingsBinding.inflate(layoutInflater)

        return binding.root
    }

}