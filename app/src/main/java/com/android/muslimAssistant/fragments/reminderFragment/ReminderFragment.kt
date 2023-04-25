package com.android.muslimAssistant.fragments.reminderFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.Toast
import androidx.core.view.get
import androidx.fragment.app.Fragment
import com.android.muslimAssistant.Timing
import com.android.muslimAssistant.database.ReminderItem
import com.android.muslimAssistant.utils.*
import com.android.muslimAssistant.R
import com.android.muslimAssistant.databinding.FragmentReminderBinding
import com.android.muslimAssistant.databinding.ReminderAlertDialogBinding
import com.android.muslimAssistant.databinding.SeekBarBinding
import com.android.muslimAssistant.databinding.TimePickerBinding
import kotlinx.coroutines.runBlocking
import org.koin.androidx.viewmodel.ext.android.viewModel

class ReminderFragment : Fragment() {

    private lateinit var binding: FragmentReminderBinding
    private val viewModel: ReminderViewModel by viewModel()

    private val itemsList = CustomList<ReminderItem>()
    private lateinit var adapter: ReminderAdapter

    private lateinit var clearListAlertDialog: AlertDialogWrapper.Builder
    private lateinit var addReminderAlertDialog: AlertDialogWrapper.Builder

    private val timing by lazy { Timing() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentReminderBinding.inflate(layoutInflater)

        adapter = ReminderAdapter(itemsList, requireContext())

        binding.reminderItemsContainer.adapter = adapter

        addReminderAlertDialog = AlertDialogWrapper.Builder(requireContext())
        clearListAlertDialog = AlertDialogWrapper.Builder(requireContext())

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
                    clearListAlertDialog.setTitle(getString(R.string.warning))
                        .setMessage(getString(R.string.areYouSureYouWantToRemoveAllReminders))
                        .setPositiveButton("Ok") {
                            var counter = 0
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
                        requireContext(), getString(R.string.listIsEmpty), Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                Toast.makeText(
                    requireContext(), getString(R.string.tryAgain), Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun addReminderListener() {
        binding.addReminder.setOnClickListener {
            if (!viewModel.isBusy.value!!) {
                showReminderConfigurations()
            } else {
                Toast.makeText(
                    requireContext(), getString(R.string.tryAgain), Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun showReminderConfigurations() {
        var toast: Toast? = null

        val reminderAlertDialogBinding = ReminderAlertDialogBinding.inflate(layoutInflater)

        reminderAlertDialogBinding.radioGroup.setOnCheckedChangeListener { group, checkedId ->
            val minutes = handleCheckedRadio(reminderAlertDialogBinding, checkedId)

            addReminderAlertDialog.setPositiveButton("Ok", false) {
                if (reminderAlertDialogBinding.description.text.isEmpty() &&
                    !reminderAlertDialogBinding.radioGroup[0].isSelected &&
                    !reminderAlertDialogBinding.radioGroup[1].isSelected
                ) {
                    toast?.cancel()
                    toast = Toast.makeText(
                        requireContext(),
                        getString(R.string.pleaseDoYourConfigurations),
                        Toast.LENGTH_SHORT
                    )
                    toast?.show()
                    return@setPositiveButton
                }

                if (checkedId == R.id.interval) {
                    itemsList.add(
                        ReminderItem(
                            (System.currentTimeMillis() % dayInMillis).toInt(),
                            reminderAlertDialogBinding.description.text.toString(),
                            minutes.value / 60,
                            minutes.value % 60,
                            buildString {
                                append(getString(R.string.Every))
                                append(" ")
                                append(getIntervalString(minutes.value))
                            },
                            false
                        )
                    )
                } else {
                    val pairOfHm = convertMinutesToHoursAndMinutes(minutes.value)
                    val hour = pairOfHm.first
                    val minute = pairOfHm.second
                    itemsList.add(
                        ReminderItem(
                            (System.currentTimeMillis() % dayInMillis).toInt(),
                            reminderAlertDialogBinding.description.text.toString(),
                            hour,
                            minute,
                            timing.convertHmTo12HrsFormat("$hour:$minute"),
                            true
                        )
                    )
                }
                adapter.notifyItemInserted(itemsList.size - 1)
                binding.reminderItemsContainer.layoutManager?.scrollToPosition(itemsList.size - 1)
                it.dismiss()
            }
        }
        addReminderAlertDialog
            .setBody(reminderAlertDialogBinding.root)
            .setTitle(resources.getString(R.string.app_name))
            .setNegativeButton("Cancel")
            .setPositiveButton("Ok", false) {
                if (reminderAlertDialogBinding.description.text.isEmpty() &&
                    !reminderAlertDialogBinding.radioGroup[0].isSelected &&
                    !reminderAlertDialogBinding.radioGroup[1].isSelected
                ) {
                    toast?.cancel()
                    toast = Toast.makeText(
                        requireContext(),
                        getString(R.string.pleaseDoYourConfigurations),
                        Toast.LENGTH_SHORT
                    )
                    toast?.show()
                    return@setPositiveButton
                }
            }
            .showDialog()
    }

    private fun handleCheckedRadio(
        binding: ReminderAlertDialogBinding,
        checkedId: Int
    ): Wrapper<Int> {
        return if (checkedId == R.id.interval) {
            val seekBarBinding = SeekBarBinding.inflate(layoutInflater)
            val progress = Wrapper(20)
            binding.container.removeAllViews()
            binding.container.addView(seekBarBinding.root)
            handleSeekBar(seekBarBinding, progress)
            progress
        } else {
            val timePickerBinding = TimePickerBinding.inflate(layoutInflater)
            val minutes = Wrapper(0)
            binding.container.removeAllViews()
            binding.container.addView(timePickerBinding.root)
            handleTimePicker(timePickerBinding, minutes)
            minutes
        }
    }

    private fun convertMinutesToHoursAndMinutes(minutes: Int): Pair<Int, Int> {
        val hours = minutes / 60
        val remainingMinutes = minutes % 60
        return Pair(hours, remainingMinutes)
    }

    fun getIntervalString(minutes: Int): String {
        val pairOfHm = convertMinutesToHoursAndMinutes(minutes)
        val hours = pairOfHm.first
        val remainingMinutes = pairOfHm.second

        var string = String()

        if (hours > 0) {
            string += buildString {
                append(hours)
                append(" ")
                append(getString(R.string.hours))
                append(" ")
            }
        }
        if (hours > 0 && remainingMinutes > 0) {
            string += getString(R.string.and) + " "
        }
        if (remainingMinutes > 0) {
            string += buildString {
                append(remainingMinutes)
                append(" ")
                append(getString(R.string.minutes))
            }
        }
        return string
    }

    private fun handleSeekBar(seekBarBinding: SeekBarBinding, progressValue: Wrapper<Int>) {

        seekBarBinding.seekBar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                seekBarBinding.seekBarValue.text = getIntervalString(p1)
                progressValue.value = p1
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {

            }

            override fun onStopTrackingTouch(p0: SeekBar?) {

            }
        })
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
                else binding.dummyText.text = getString(R.string.listIsEmpty)
            }
        }
    }

    private fun handleTimePicker(timePickerBinding: TimePickerBinding, minutes: Wrapper<Int>) {
        minutes.value = timePickerBinding.timePicker.hour * 60 + timePickerBinding.timePicker.minute
        if (timePickerBinding.timePicker.is24HourView) {
            minutes.value += 12 * 60
        }

        timePickerBinding.timePicker.setOnTimeChangedListener { view, hourOfDay, minute ->

            minutes.value = hourOfDay * 60 + minute

            if (timePickerBinding.timePicker.is24HourView) {
                minutes.value += 12 * 60
            }
        }
    }
}