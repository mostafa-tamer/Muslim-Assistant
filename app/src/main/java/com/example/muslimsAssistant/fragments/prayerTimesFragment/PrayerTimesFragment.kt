package com.example.muslimsAssistant.fragments.prayerTimesFragment

import android.Manifest
import android.annotation.SuppressLint
import android.content.IntentSender
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.example.muslimsAssistant.LocationCodes
import com.example.muslimsAssistant.PermissionsCodes
import com.example.muslimsAssistant.Timing
import com.example.muslimsAssistant.database.PrayerTimes
import com.example.muslimsAssistant.databinding.FragmentPrayerTimesBinding
import com.example.muslimsAssistant.notifications.AlarmManagerHelper
import com.example.muslimsAssistant.utils.toastErrorMessageObserver
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import kotlinx.coroutines.runBlocking
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*
import kotlin.math.abs

class PrayerTimesFragment : Fragment() {

    private lateinit var binding: FragmentPrayerTimesBinding
    private val viewModel: PrayerTimesViewModel by viewModel()
    private lateinit var prayers: List<PrayerTimes>
    private val timer by lazy { Timer() }
    private val timing by lazy { Timing() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentPrayerTimesBinding.inflate(layoutInflater)
        binding.lifecycleOwner = this
        binding.homeViewModel = viewModel

        prayers = runBlocking { viewModel.retPrayerTimesSuspend() }


        setAlarmManagerAndUpdateUI()

        observers()
        listeners()
        checkPermissions()

        return binding.root
    }


    private fun requestLocationService() {
        val locationRequest = LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)

        val client: SettingsClient = LocationServices.getSettingsClient(requireActivity())

        val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())

        task.addOnSuccessListener {

        }.addOnFailureListener { exception ->
            if (exception is ResolvableApiException) {
                try {
                    // Show the dialog by calling startResolutionForResult(),
                    // and check the result in onActivityResult()
                    exception.startResolutionForResult(
                        requireActivity(),
                        LocationCodes.REQUEST_LOCATION_SERVICE.code
                    )
                } catch (sendEx: IntentSender.SendIntentException) {
                    // Ignore the error
                }
            }
        }
    }

    private fun listeners() {
        val prayerTimes: PrayerTimes? = getPrayerTimesSelectedData(timing.getTodayDate())
        var toast: Toast? = null

        binding.fajrRow.setOnClickListener {
            prayerTimes?.let {
                toast?.cancel()
                toast = Toast.makeText(
                    requireContext(),
                    getRemainingAgo(prayerTimes.fajr),
                    Toast.LENGTH_SHORT
                )
                toast?.show()
            }
        }

        binding.sunriseRow.setOnClickListener {
            prayerTimes?.let {
                toast?.cancel()
                toast = Toast.makeText(
                    requireContext(),
                    getRemainingAgo(prayerTimes.sunrise),
                    Toast.LENGTH_SHORT
                )
                toast?.show()
            }
        }

        binding.dhuhrRow.setOnClickListener {
            prayerTimes?.let {
                toast?.cancel()
                toast = Toast.makeText(
                    requireContext(),
                    getRemainingAgo(prayerTimes.dhuhr),
                    Toast.LENGTH_SHORT
                )
                toast?.show()
            }
        }

        binding.asrRow.setOnClickListener {
            prayerTimes?.let {
                toast?.cancel()
                toast = Toast.makeText(
                    requireContext(),
                    getRemainingAgo(prayerTimes.asr),
                    Toast.LENGTH_SHORT
                )
                toast?.show()
            }
        }

        binding.maghribRow.setOnClickListener {
            prayerTimes?.let {
                toast?.cancel()
                toast = Toast.makeText(
                    requireContext(),
                    getRemainingAgo(prayerTimes.maghrib),
                    Toast.LENGTH_SHORT
                )
                toast?.show()
            }
        }

        binding.ishaRow.setOnClickListener {
            prayerTimes?.let {
                toast?.cancel()
                toast = Toast.makeText(
                    requireContext(),
                    getRemainingAgo(prayerTimes.isha),
                    Toast.LENGTH_SHORT
                )
                toast?.show()
            }
        }
    }

    private fun getRemainingAgo(prayerTime: String): String {
        val prayerTimeMillis =
            timing.convertDmyHmToMillis("${timing.getTodayDate()} $prayerTime")
        val currentTimeMillis = System.currentTimeMillis()
        return if (prayerTimeMillis > currentTimeMillis) {
            val remainingTimeInMillis = prayerTimeMillis - currentTimeMillis
            timing.convertMillisToHm(
                remainingTimeInMillis,
                "H 'hrs and' m 'min remaining'"
            )
        } else {
            val remainingTimeInMillis = abs(prayerTimeMillis - currentTimeMillis)
            timing.convertMillisToHm(
                remainingTimeInMillis,
                "H 'hrs and' mm 'min ago'"
            )
        }
    }

    private fun observers() {
        isLoadingDataBusyObserver()
        updateUIObserver()
        toastErrorMessageObserver(
            viewModel.errorMessageLiveData,
            viewLifecycleOwner,
            requireContext()
        )
    }

    private fun updateUIObserver() {
        viewModel.isDataLoadedObserver.observe(viewLifecycleOwner) {
            if (it) {
                prayers = runBlocking { viewModel.retPrayerTimesSuspend() }
                setAlarmManagerAndUpdateUI()
                listeners()
            }
        }
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

    private fun setAlarmManagerAndUpdateUI() {
        updateUI()
        val alarmManagerHelper = AlarmManagerHelper(requireContext())
        alarmManagerHelper.scheduleAlarms()
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
        requestLocationService()
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

    private fun getPrayerTimesSelectedData(
        date: String
    ): PrayerTimes? {
        val searchIndex =
            prayers.binarySearch { it.dateGregorian.compareTo(date) }
        if (searchIndex >= 0) {
            return prayers[searchIndex]
        }
        return null
    }

    private fun updateUI() {
        updatePrayerTimesContainer()
        scheduleRemainingTimeTillNextPrayerTime()
    }

    private fun scheduleRemainingTimeTillNextPrayerTime() {
        timer.schedule(
            object : TimerTask() {
                override fun run() {
                    setRemainingTime()
                }
            }, 0, 1000
        )
    }

    private fun updatePrayerTimesContainer() {
        val prayerTimes = getPrayerTimesSelectedData(timing.getTodayDate())
        if (prayerTimes != null) {
            viewModel.dateHijri.value = prayerTimes.dateHigri
            viewModel.monthHijri.value = prayerTimes.monthHijri
            viewModel.fajr.value = timing.convertHmTo12HrsFormat(prayerTimes.fajr)
            viewModel.sunrise.value = timing.convertHmTo12HrsFormat(prayerTimes.sunrise)
            viewModel.dhuhr.value = timing.convertHmTo12HrsFormat(prayerTimes.dhuhr)
            viewModel.asr.value = timing.convertHmTo12HrsFormat(prayerTimes.asr)
            viewModel.maghrib.value = timing.convertHmTo12HrsFormat(prayerTimes.maghrib)
            viewModel.isha.value = timing.convertHmTo12HrsFormat(prayerTimes.isha)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        timer.cancel()
    }

    private fun setRemainingTime() {
        val todayPrayerTimes = getPrayerTimesSelectedData(timing.getTodayDate())
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
                    timing.convertDmyHmToMillis("${timing.getTodayDate()} ${i.key}")
                val currentTimeMillis = System.currentTimeMillis()

                if (currentTimeMillis < prayerTimeMillis) {
                    binding.remaining.text = "الوقت المتبقي على صلاة ${i.value}"
                    val remainingTimeInMillis = prayerTimeMillis - currentTimeMillis
//                    val remainingTimeString = timing.convertMillisToHMS(
//                        remainingTimeInMillis,
//                        "H 'ساعة، ' m 'دقيقة و' s 'ثانية'"
//                    )
                    val remainingTimeString = timing.convertMillisToHMS(
                        remainingTimeInMillis,
                        "HH:mm:ss"
                    )

                    binding.remainingTime.text = remainingTimeString
                    foundToday = true
                    break
                }
            }
        }

        if (foundToday) return

        binding.remaining.text = "الوقت المتبقي على صلاة الفجر"

        val tomorrowPrayerTimes = getPrayerTimesSelectedData(timing.getTomorrowDate())

        if (tomorrowPrayerTimes != null) {
            val tomorrowFajrTimeMillis =
                timing.convertDmyHmToMillis("${timing.getTomorrowDate()} ${tomorrowPrayerTimes.fajr}")
            val currentTimeMillis = System.currentTimeMillis()

            if (currentTimeMillis < tomorrowFajrTimeMillis) {
                val remainingTimeInMillis = tomorrowFajrTimeMillis - currentTimeMillis
//                val remainingTimeString = timing.convertMillisToHMS(
//                    remainingTimeInMillis,
//                    "H 'ساعة، ' m 'دقيقة و' s 'ثانية'"
//                )
                val remainingTimeString = timing.convertMillisToHMS(
                    remainingTimeInMillis,
                    "HH:mm:ss"
                )
                binding.remainingTime.text = remainingTimeString
            }
        }
    }
}