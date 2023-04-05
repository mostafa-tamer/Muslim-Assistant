package com.example.muslimsAssistant.fragments.prayerTimesFragment

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.example.muslimsAssistant.PermissionsCodes
import com.example.muslimsAssistant.Timing.convertDateTimeNoSecToMillisNoSec
import com.example.muslimsAssistant.Timing.convertMillisToHMS
import com.example.muslimsAssistant.Timing.convertTo12HourFormat
import com.example.muslimsAssistant.Timing.getTodayDate
import com.example.muslimsAssistant.Timing.getTomorrowDate
import com.example.muslimsAssistant.database.PrayerTimes
import com.example.muslimsAssistant.databinding.FragmentPrayerTimesBinding
import com.example.muslimsAssistant.notifications.AlarmManagerHelper
import com.example.muslimsAssistant.utils.CustomAlertDialog
import com.example.muslimsAssistant.utils.toastErrorMessageObserver
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.runBlocking
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*

class PrayerTimesFragment : Fragment() {

    private lateinit var binding: FragmentPrayerTimesBinding
    private val viewModel: PrayerTimesViewModel by viewModel()

    private lateinit var exceptionErrorMessageAlertDialog: CustomAlertDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentPrayerTimesBinding.inflate(layoutInflater)
        binding.lifecycleOwner = this
        binding.homeViewModel = viewModel


        exceptionErrorMessageAlertDialog = CustomAlertDialog(requireContext())

//        prayerTimesDatabaseLoader()
        isLoadingDataBusyObserver()

        toastErrorMessageObserver(
            viewModel.errorMessageLiveData,
            viewLifecycleOwner,
            requireContext()
        )

        checkPermissions()

        return binding.root
    }

    private fun isLoadingDataBusyObserver() {
        viewModel.isLoadingPrayerTimes.observe(viewLifecycleOwner) {
            if (it == false) {
                binding.progressBar.visibility = View.INVISIBLE
                prayerTimesDatabaseLoader()
            } else {
                binding.progressBar.visibility = View.VISIBLE
            }
        }
    }

    private fun prayerTimesDatabaseLoader() {
        runBlocking {
            val prayerTimes = viewModel.retPrayerTimesSuspend()
            updateViewModelPrayerTimes(prayerTimes)
            val alarmManagerHelper = AlarmManagerHelper(requireContext())
            alarmManagerHelper.setPrayerTimes()
        }
    }

    @Suppress("DEPRECATION")
    private fun checkPermissions() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ), PermissionsCodes.ACCESS_COARSE_LOCATION.code
            )
        } else {
            trackUserLocation()
        }
    }

    @SuppressLint("MissingPermission")
    private fun trackUserLocation() {
        LocationServices
            .getFusedLocationProviderClient(requireActivity())
            .lastLocation
            .addOnSuccessListener {
                if (it != null) {
                    println("LatLng ${it.latitude} ${it.longitude}")
                    viewModel.updateUserLocationAndPrayerTimes(
                        it.latitude,
                        it.longitude
                    )
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Check the internet and location",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        if (requestCode == PermissionsCodes.ACCESS_COARSE_LOCATION.code &&
            grantResults.isNotEmpty() &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            trackUserLocation()
        }
    }

    fun getPrayerTimesSelectedData(prayerTimesList: List<PrayerTimes>, date: String): PrayerTimes? {
        val searchIndex =
            prayerTimesList.binarySearch { it.dateGregorian.compareTo(date) }
        if (searchIndex >= 0) {
            return prayerTimesList[searchIndex]
        }
        return null
    }

    private fun updateViewModelPrayerTimes(prayerTimesList: List<PrayerTimes>) {
        val prayerTimes = getPrayerTimesSelectedData(prayerTimesList, getTodayDate())
        if (prayerTimes != null) {
            viewModel.dateHijri.value =  prayerTimes.dateHigri
            viewModel.monthHijri.value =  prayerTimes.monthHijri
            viewModel.fajr.value = convertTo12HourFormat(prayerTimes.fajr)
            viewModel.sunrise.value = convertTo12HourFormat(prayerTimes.sunrise)
            viewModel.dhuhr.value = convertTo12HourFormat(prayerTimes.dhuhr)
            viewModel.asr.value = convertTo12HourFormat(prayerTimes.asr)
            viewModel.maghrib.value = convertTo12HourFormat(prayerTimes.maghrib)
            viewModel.isha.value = convertTo12HourFormat(prayerTimes.isha)

            Timer().schedule(
                object : TimerTask() {
                    override fun run() {
                        setRemainingTime(prayerTimesList)
                    }
                }, 0, 1000
            )
        }
    }

    private fun setRemainingTime(prayerTimesList: List<PrayerTimes>) {
        val todayPrayerTimes = getPrayerTimesSelectedData(prayerTimesList, getTodayDate())
        var foundToday = false
        if (todayPrayerTimes != null) {
            val prayerTimesStrings = mapOf(
                todayPrayerTimes.fajr to "الفجر",
                todayPrayerTimes.dhuhr to "الظهر",
                todayPrayerTimes.asr to "العصر",
                todayPrayerTimes.maghrib to "المغرب",
                todayPrayerTimes.isha to "العشاء",
            )
            for (i in prayerTimesStrings) {

                val prayerTimeMillis =
                    convertDateTimeNoSecToMillisNoSec("${getTodayDate()} ${i.key}")

                val currentTimeMillis = System.currentTimeMillis()

                if (currentTimeMillis < prayerTimeMillis) {

                    binding.remaining.text = "الوقت المتبقي على صلاة ${i.value}"

                    val remainingTimeInMillis = prayerTimeMillis - currentTimeMillis
                    val remainingTimeString = convertMillisToHMS(remainingTimeInMillis)
                    binding.remainingTime.text = remainingTimeString
                    foundToday = true
                    break
                }
            }
        }

        if (foundToday) return

        binding.remaining.text = "الوقت المتبقي على صلاة الفجر"

        val tomorrowPrayerTimes = getPrayerTimesSelectedData(prayerTimesList, getTomorrowDate())

        if (tomorrowPrayerTimes != null) {
            val tomorrowFajrTimeMillis =
                convertDateTimeNoSecToMillisNoSec("${getTomorrowDate()} ${tomorrowPrayerTimes.fajr}")
            val currentTimeMillis = System.currentTimeMillis()

            if (currentTimeMillis < tomorrowFajrTimeMillis) {
                val remainingTimeInMillis = tomorrowFajrTimeMillis - currentTimeMillis
                val remainingTimeString = convertMillisToHMS(remainingTimeInMillis)
                binding.remainingTime.text = remainingTimeString
            }
        }
    }
}