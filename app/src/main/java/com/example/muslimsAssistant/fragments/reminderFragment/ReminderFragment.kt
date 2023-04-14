package com.example.muslimsAssistant.fragments.reminderFragment

import android.os.Bundle
import android.text.format.DateFormat.is24HourFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.muslimsAssistant.R
import com.example.muslimsAssistant.Timing
import com.example.muslimsAssistant.database.ReminderItem
import com.example.muslimsAssistant.databinding.EditTextBinding
import com.example.muslimsAssistant.databinding.FragmentReminderBinding
import com.example.muslimsAssistant.utils.CustomAlertDialog
import com.example.muslimsAssistant.utils.CustomList
import com.example.muslimsAssistant.utils.cancelPendingIntent
import com.example.muslimsAssistant.utils.dayInMillis
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import kotlinx.coroutines.runBlocking
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*

class ReminderFragment : Fragment() {

    private lateinit var binding: FragmentReminderBinding
    private val viewModel: ReminderViewModel by viewModel()

    private val itemsList = CustomList<ReminderItem>()
    private lateinit var adapter: ReminderAdapter

    private lateinit var clearListAlertDialog: CustomAlertDialog
    private lateinit var addReminderAlertDialog: CustomAlertDialog

    private val timing by lazy { Timing() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentReminderBinding.inflate(layoutInflater)

        adapter = ReminderAdapter(itemsList, requireContext())

        binding.reminderItemsContainer.adapter = adapter

        addReminderAlertDialog = CustomAlertDialog(requireContext())
        clearListAlertDialog = CustomAlertDialog(requireContext())

        fillItemsListFromDb()
        listSizeObserver()
        addReminderListener()
        removeAllListener()

        return binding.root
    }

    private fun removeAllListener() {
        binding.removeAll.setOnClickListener {
            if (!viewModel.isBusy.value!!) {
                if (itemsList.isNotEmpty()) {
                    clearListAlertDialog.setTitle("Warning")
                        .setMessage("Are you sure you want to remove all reminders?")
                        .setPositiveButton("Ok") {
                            var counter = 0;
                            while (counter < itemsList.size) {
                                cancelPendingIntent(itemsList[counter].id, requireContext())
                                println("${itemsList[counter]} is canceled")
                                counter++
                            }
                            itemsList.clear()
                            adapter.notifyDataSetChanged()
                        }.setNegativeButton("Cancel").showDialog()
                } else {
                    Toast.makeText(
                        requireContext(), "List is empty", Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                Toast.makeText(
                    requireContext(), "Try again...", Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun addReminderListener() {
        binding.addReminder.setOnClickListener {
            if (!viewModel.isBusy.value!!) {
                val editText = EditTextBinding.inflate(layoutInflater)
                addReminderAlertDialog.setBody(editText.root)
                    .setTitle(resources.getString(R.string.app_name))
                    .setPositiveButton("Ok", false) {
                        if (editText.description.text.isNotEmpty()) {
                            openTimePicker(editText.description.text.toString())
                            it.dismiss()
                        } else {
                            Toast.makeText(
                                requireContext(), "Please fill the description", Toast.LENGTH_SHORT
                            ).show()
                        }
                    }.setNegativeButton("Cancel").showDialog()
            } else {
                Toast.makeText(
                    requireContext(), "Try again...", Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun fillItemsListFromDb() {
        runBlocking {
            itemsList.addAll(viewModel.retData())
        }
    }

    private fun listSizeObserver() {
        itemsList.sizeLiveData.observe(viewLifecycleOwner) {
            it?.let {
                viewModel.updateDB(itemsList, requireContext())
                if (it > 0) binding.dummyText.text = ""
                else binding.dummyText.text = "List Is Empty."
            }
        }
    }


    private fun openTimePicker(text: String) {
        val is24HourFormat = is24HourFormat(requireContext())
        val clockFormat: Int = if (is24HourFormat) TimeFormat.CLOCK_24H else TimeFormat.CLOCK_12H

        val calendar = Calendar.getInstance()
        val hour: Int = calendar.get(Calendar.HOUR_OF_DAY)
        val minute: Int = calendar.get(Calendar.MINUTE)


        val picker = MaterialTimePicker.Builder()
            .setTimeFormat(clockFormat).setHour(hour).setMinute(minute).setTitleText("Set Reminder")
            .build()

        picker.show(childFragmentManager, "TAG")

        picker.addOnPositiveButtonClickListener {

            var hourString = picker.hour.toString()
            var minuteString = picker.minute.toString()
            if (hourString.length == 1) {
                hourString = "0$hourString"
            }
            if (minuteString.length == 1) {
                minuteString = "0$minuteString"
            }

            val timeString = timing.convertHmTo12HrsFormat("$hourString:$minuteString")

            itemsList.add(
                ReminderItem(
                    (System.currentTimeMillis() % dayInMillis).toInt(),
                    text,
                    picker.hour,
                    picker.minute,
                    timeString
                )
            )
            adapter.notifyItemInserted(itemsList.size - 1)
            binding.reminderItemsContainer.layoutManager?.scrollToPosition(itemsList.size - 1)
        }
    }
}