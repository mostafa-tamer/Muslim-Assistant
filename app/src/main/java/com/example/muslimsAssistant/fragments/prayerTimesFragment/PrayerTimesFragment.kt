package com.example.muslimsAssistant.fragments.prayerTimesFragment

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.example.muslimsAssistant.Timing
import com.example.muslimsAssistant.database.PrayerTimes
import com.example.muslimsAssistant.databinding.FragmentPrayerTimesBinding
import com.example.muslimsAssistant.notifications.AlarmManagerHelper
import com.example.muslimsAssistant.utils.CustomAlertDialog
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
    private var todayPrayerTimes: PrayerTimes? = null
    private var tomorrowPrayerTimes: PrayerTimes? = null

    private lateinit var warningAlertDialog: CustomAlertDialog

    private val timer by lazy { Timer() }
    private val timing by lazy { Timing() }

    private val requestPermissionLauncher: ActivityResultLauncher<String> =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                println("Location is granted")
                requestLocationService()
            } else {
                println("Location is not granted")

                warningAlertDialog.setTitle("Warning")
                    .setMessage("You should enable location permission to use get the prayer times")
                    .setCancelable(false)
                    .setPositiveButton("Ok") {
                        checkPermissions()
                    }.showDialog()
            }
        }

    private val locationSettingsResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            println("User pressed Ok to enable location")
            trackUserLocation()
        } else {
            println("Location service is not granted")
            warningAlertDialog.setTitle("Warning")
                .setMessage("You should enable location service to use get the prayer times")
                .setCancelable(false)
                .setPositiveButton("Ok") {
                    checkPermissions()
                }.showDialog()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentPrayerTimesBinding.inflate(layoutInflater)
        binding.lifecycleOwner = this
        binding.homeViewModel = viewModel

        prayers = runBlocking { viewModel.retPrayerTimesSuspend() }

        todayPrayerTimes = getPrayerTimesSelectedData(timing.getTodayDate())
        tomorrowPrayerTimes = getPrayerTimesSelectedData(timing.getTomorrowDate())

        warningAlertDialog = CustomAlertDialog(requireContext())

        observers()
        listeners()
 
        checkPermissions()
        updateUI()
        schedulePrayerTimesNotifications()


        return binding.root
    }

    private fun schedulePrayerTimesNotifications() {
        AlarmManagerHelper(requireContext()).scheduleAlarms()
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
            trackUserLocation()
            println("Location is enabled")
        }.addOnFailureListener { exception ->
            if (exception is ResolvableApiException) {
                try {
                    locationSettingsResultLauncher.launch(
                        IntentSenderRequest.Builder(exception.resolution).build()
                    )
                } catch (_: Exception) {
                }
            }
        }
    }

    private fun createPrayerRowListener(prayerTime: String): ((View) -> Unit) {
        return {
            todayPrayerTimes?.let {
                Toast.makeText(
                    requireContext(),
                    getRemainingAgo(prayerTime),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun listeners() {
        todayPrayerTimes?.let {
            binding.fajrRow.setOnClickListener(createPrayerRowListener(it.fajr))
            binding.sunriseRow.setOnClickListener(createPrayerRowListener(it.sunrise))
            binding.dhuhrRow.setOnClickListener(createPrayerRowListener(it.dhuhr))
            binding.asrRow.setOnClickListener(createPrayerRowListener(it.asr))
            binding.maghribRow.setOnClickListener(createPrayerRowListener(it.maghrib))
            binding.ishaRow.setOnClickListener(createPrayerRowListener(it.isha))
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
        isDataLoadingObserver()
        prayerTimesDatabaseObserver()
        toastErrorMessageObserver(
            viewModel.errorMessageLiveData,
            viewLifecycleOwner,
            requireContext()
        )
    }

    private fun prayerTimesDatabaseObserver() {
        viewModel.retPrayerTimesLiveData().observe(viewLifecycleOwner) {
            prayers = it
            todayPrayerTimes = getPrayerTimesSelectedData(timing.getTodayDate())
            tomorrowPrayerTimes = getPrayerTimesSelectedData(timing.getTomorrowDate())

            updateUI()
            schedulePrayerTimesNotifications()
        }
    }

    private fun isDataLoadingObserver() {
        viewModel.isLoadingPrayerTimes.observe(viewLifecycleOwner) {
            if (it == false) {
                binding.progressBar.visibility = View.INVISIBLE
            } else {
                binding.progressBar.visibility = View.VISIBLE
            }
        }
    }


    private fun checkPermissions() {
        requestPermissionLauncher.launch(Manifest.permission.ACCESS_COARSE_LOCATION)
    }

    @SuppressLint("MissingPermission")
    private fun trackUserLocation() {
        val locationRequest = LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            interval = 1000 // Request location updates every 10 seconds
        }

        val fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireActivity())

        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest,
            object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    super.onLocationResult(locationResult)

                    locationResult.lastLocation.let { location ->
                        println("LatLng ${location.latitude} ${location.longitude}")
                        viewModel.updateUserLocationAndPrayerTimes(
                            location.latitude,
                            location.longitude
                        )
                        fusedLocationProviderClient.removeLocationUpdates(this)
                    }
                }
            },
            Looper.getMainLooper()
        )
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
        todayPrayerTimes?.let {
            viewModel.dateHijri.value = it.dateHigri
            viewModel.monthHijri.value = it.monthHijri
            viewModel.fajr.value = timing.convertHmTo12HrsFormat(it.fajr)
            viewModel.sunrise.value = timing.convertHmTo12HrsFormat(it.sunrise)
            viewModel.dhuhur.value = timing.convertHmTo12HrsFormat(it.dhuhr)
            viewModel.asr.value = timing.convertHmTo12HrsFormat(it.asr)
            viewModel.maghrib.value = timing.convertHmTo12HrsFormat(it.maghrib)
            viewModel.isha.value = timing.convertHmTo12HrsFormat(it.isha)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        timer.cancel()
    }

    private fun setRemainingTime() {
        var foundToday = false
        todayPrayerTimes?.let {
            val prayerTimesStrings = mapOf(
                it.fajr to "الفجر",
                it.dhuhr to "الظهر",
                it.asr to "العصر",
                it.maghrib to "المغرب",
                it.isha to "العشاء",
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

        tomorrowPrayerTimes?.let {
            val tomorrowFajrTimeMillis =
                timing.convertDmyHmToMillis("${timing.getTomorrowDate()} ${it.fajr}")
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