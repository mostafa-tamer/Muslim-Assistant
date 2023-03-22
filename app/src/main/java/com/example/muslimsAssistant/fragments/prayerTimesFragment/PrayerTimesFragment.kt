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
import com.example.muslimsAssistant.Timing.getTodayDate
import com.example.muslimsAssistant.database.PrayerTimes
import com.example.muslimsAssistant.databinding.FragmentPrayerTimesBinding
import com.example.muslimsAssistant.notifications.AlarmManagerHelper
import com.example.muslimsAssistant.utils.CustomAlertDialog
import com.example.muslimsAssistant.utils.toastErrorMessageObserver
import com.google.android.gms.location.LocationServices
import org.koin.androidx.viewmodel.ext.android.viewModel

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

        databaseObserver()
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
            } else {
                binding.progressBar.visibility = View.VISIBLE
            }
        }
    }

    private fun databaseObserver() {
        viewModel.retPrayerTimesLiveData().observe(viewLifecycleOwner) {
            if (it != null) {
                updateViewModelPrayerTimes(it)
                val alarmManagerHelper = AlarmManagerHelper(requireContext())
                alarmManagerHelper.setPrayerTimes()
            }
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

    private fun updateViewModelPrayerTimes(prayerTimesList: List<PrayerTimes>) {
        val searchIndex = prayerTimesList.binarySearch { it.date.compareTo(getTodayDate()) }
        if (searchIndex >= 0) {
            val prayerTime = prayerTimesList[searchIndex]
            viewModel.date.value = prayerTime.date
            viewModel.fajr.value = prayerTime.fajr
            viewModel.sunrise.value = prayerTime.sunrise
            viewModel.dhuhr.value = prayerTime.dhuhr
            viewModel.asr.value = prayerTime.asr
            viewModel.maghrib.value = prayerTime.maghrib
            viewModel.isha.value = prayerTime.isha
        }
    }
}