package com.android.muslimAssistant.fragments.tasbeehFragment

import android.content.Context
import android.os.Bundle
import android.os.Vibrator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import com.android.muslimAssistant.R
import com.android.muslimAssistant.databinding.FragmentTasbeehBinding
import com.android.muslimAssistant.repository.TasbeehFragmentRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.koin.java.KoinJavaComponent.get


class TasbeehFragment : Fragment() {

    lateinit var binding: FragmentTasbeehBinding
    private val tasbeehFragmentRepository: TasbeehFragmentRepository by lazy {
        get(TasbeehFragmentRepository::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentTasbeehBinding.inflate(layoutInflater)


        initializeUI()
        resetCounter()
        incrementCounter()

        return binding.root
    }

    private fun initializeUI() {
        runBlocking {
            val counter = tasbeehFragmentRepository.counterValue.first()
            val highestValue = tasbeehFragmentRepository.highestValue.first()
            binding.progressBar.progress = counter
            binding.highestScoreText.text =
                buildString {
                    append(getString(R.string.highest_dhikr))
                    append(" ")
                    append(highestValue.toString())
                }
            binding.score.text = counter.toString()
            binding.progressBar.max = tasbeehFragmentRepository.indicatorMax.first().toInt()
            binding.progressBar.progress = counter
            checkIndicatorMax(counter)
        }
    }

    private fun resetCounter() {
        binding.tasbeehCounterReset.setOnClickListener {
            runBlocking {
                tasbeehFragmentRepository.resetCounter()
                binding.score.text = "0"
                binding.progressBar.progress = 0
            }
        }
    }

    private fun incrementCounter() {
        binding.root.setOnClickListener {
            runBlocking {
                tasbeehFragmentRepository.incrementCounter()
                tasbeehFragmentRepository.highestValueIncrement()
                val counter = tasbeehFragmentRepository.counterValue.first()
                val highestValue = tasbeehFragmentRepository.highestValue.first()
                binding.score.text = counter.toString()
                binding.highestScoreText.text =
                    buildString {
                        append(getString(R.string.highest_dhikr))
                        append(" ")
                        append(highestValue.toString())
                    }
                binding.progressBar.progress = counter
                checkIndicatorMax(counter)
            }
        }
    }

    private fun checkIndicatorMax(counter: Int) {
        if (counter == binding.progressBar.max) {
            val vibrator = getSystemService(requireContext(), Vibrator::class.java) as Vibrator
            vibrator.vibrate(200)
        }

        if (counter >= binding.progressBar.max) {
            binding.progressBar.setIndicatorColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.green
                )
            )
        } else {
            binding.progressBar.setIndicatorColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.blue
                )
            )
        }
    }
}