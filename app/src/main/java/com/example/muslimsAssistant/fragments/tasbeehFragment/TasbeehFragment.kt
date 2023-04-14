package com.example.muslimsAssistant.fragments.tasbeehFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.muslimsAssistant.databinding.FragmentTasbeehBinding
import kotlinx.coroutines.runBlocking
import org.koin.androidx.viewmodel.ext.android.viewModel


class TasbeehFragment : Fragment() {

    lateinit var binding: FragmentTasbeehBinding
    private var counter = 0
    private val viewModel: TasbeehViewModel by viewModel()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentTasbeehBinding.inflate(layoutInflater)

        initializeCounter()

        resetCounter()
        tasbeehCounter()

        return binding.root
    }

    private fun initializeCounter() {
        runBlocking {
            counter = viewModel.retTasbeehCounter()?.tasbeehCounter ?: 0
            binding.score.text = counter.toString()
        }
    }

    private fun resetCounter() {
        binding.tasbeehCounterReset.setOnClickListener {
            runBlocking {
                counter = 0
                viewModel.updateTasbeehCounter(0)
                binding.score.text = "0"
            }
        }
    }

    private fun tasbeehCounter() {
        binding.root.setOnClickListener {
            runBlocking {
                binding.score.text = (++counter).toString()
                viewModel.updateTasbeehCounter(counter)
            }
        }
    }

}