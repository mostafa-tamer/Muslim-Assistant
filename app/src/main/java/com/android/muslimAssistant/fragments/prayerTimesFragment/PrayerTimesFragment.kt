package com.android.muslimAssistant.fragments.prayerTimesFragment

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Intent
import android.os.Build
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
import androidx.lifecycle.lifecycleScope
import com.android.muslimAssistant.R
import com.android.muslimAssistant.Timing
import com.android.muslimAssistant.database.PrayerTimes
import com.android.muslimAssistant.databinding.FragmentPrayerTimesBinding
import com.android.muslimAssistant.notifications.AlarmHandler
import com.android.muslimAssistant.repository.SharedPreferencesRepository
import com.android.muslimAssistant.utils.AlertDialogWrapper
import com.android.muslimAssistant.utils.startNewService
import com.android.muslimAssistant.utils.toast
import com.android.muslimAssistant.utils.toastErrorMessageObserver
import com.android.muslimAssistant.widgets.PrayerTimesWidget
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.koin.android.ext.android.get
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*
import kotlin.math.abs

class PrayerTimesFragment : Fragment() {

    private lateinit var binding: FragmentPrayerTimesBinding
    private val viewModel: PrayerTimesViewModel by viewModel()

    private lateinit var prayers: List<PrayerTimes>
    private var todayPrayerTimes: PrayerTimes? = null
    private var tomorrowPrayerTimes: PrayerTimes? = null

    private lateinit var alertDialogWrapper: AlertDialogWrapper.Builder
    private val sharedPreferencesRepository: SharedPreferencesRepository by lazy { get() }

    private lateinit var warningAlertDialog: AlertDialogWrapper.Builder

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
                warningAlertDialog.setTitle(getString(R.string.warning))
                    .setMessage(getString(R.string.locationPermissionRequest))
                    .setCancelable(false)
                    .setPositiveButton(getString(R.string.Ok)) {
                        checkPermissions()
                    }.showDialog()
            }
        }

    private val locationSettingsResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            println("User pressed Ok to enable location")
            run()
        } else {
            println(getString(R.string.LocationServiceIsNotGranted))
            warningAlertDialog.setTitle(getString(R.string.warning))
                .setMessage(getString(R.string.locationServiceRequest))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.Ok)) {
                    checkPermissions()
                }.showDialog()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentPrayerTimesBinding.inflate(layoutInflater)

        alertDialogWrapper = AlertDialogWrapper.Builder(requireContext())
        warningAlertDialog = AlertDialogWrapper.Builder(requireContext())

        checkPermissions()

        return binding.root
    }


    private fun handleAutoStart() {
        if (isAutoStartEnabled())
            return
        alertDialogWrapper.setTitle(getString(R.string.warning))
            .setMessage(getString(R.string.applyAutoStartString))
            .setPositiveButton(getString(R.string.Ok)) {
                lifecycleScope.launch {
                    sharedPreferencesRepository.updateIsAutoStarted(true)
                }
                try {
                    val intent = Intent()
                    val manufacturer = Build.MANUFACTURER
                    if ("xiaomi".equals(manufacturer, ignoreCase = true)) {
                        intent.component = ComponentName(
                            "com.miui.securitycenter",
                            "com.miui.permcenter.autostart.AutoStartManagementActivity"
                        )
                    } else if ("oppo".equals(manufacturer, ignoreCase = true)) {
                        intent.component = ComponentName(
                            "com.coloros.safecenter",
                            "com.coloros.safecenter.permission.startup.StartupAppListActivity"
                        )
                    } else if ("vivo".equals(manufacturer, ignoreCase = true)) {
                        intent.component = ComponentName(
                            "com.vivo.permissionmanager",
                            "com.vivo.permissionmanager.activity.BgStartUpManagerActivity"
                        )
                    } else if ("huawei".equals(manufacturer, ignoreCase = true)) {
                        intent.component = ComponentName(
                            "com.huawei.systemmanager",
                            "com.huawei.systemmanager.optimize.process.ProtectActivity"
                        )
                    } else {
                        toast?.cancel()
                        toast = Toast.makeText(
                            requireContext(),
                            getString(R.string.doAutoStartManually),
                            Toast.LENGTH_SHORT
                        )
                        toast?.show()
                        return@setPositiveButton
                    }
                    startActivity(intent);
                } catch (e: Exception) {
                    println(e.message)
                }
            }.setNegativeButton(getString(R.string.cancel))
            .showDialog()
    }

    private fun isAutoStartEnabled(): Boolean = runBlocking {
        sharedPreferencesRepository.isAutoStarted.first()
    }

    private fun schedulePrayerTimesNotifications() {
        AlarmHandler(requireContext()).scheduleAlarms()
    }

    private fun updateWidget() {
        val appWidgetManager = AppWidgetManager.getInstance(requireContext())
        val componentName = ComponentName(requireContext(), PrayerTimesWidget::class.java)
        val appWidgetIds = appWidgetManager.getAppWidgetIds(componentName)
        val widget = PrayerTimesWidget()
        widget.onUpdate(requireContext(), appWidgetManager, appWidgetIds)
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
            run()
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
                toast?.cancel()
                toast = Toast.makeText(
                    requireContext(),
                    getRemainingAgo(prayerTime),
                    Toast.LENGTH_SHORT
                )
                toast?.show()
            }
        }
    }

    private fun listeners() {
        todayPrayerTimes?.let {
            binding.fajrRow.setOnClickListener(createPrayerRowListener(it.fajr))
            binding.sunriseRow.setOnClickListener(createPrayerRowListener(it.sunrise))
            binding.dhuhrRow.setOnClickListener(createPrayerRowListener(it.dhuhur))
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
            timing.convertMillisToHmUTC(
                remainingTimeInMillis,
                getString(R.string.remainingTimeFormat)
            )
        } else {
            val remainingTimeInMillis = abs(prayerTimeMillis - currentTimeMillis)
            timing.convertMillisToHmUTC(
                remainingTimeInMillis,
                getString(R.string.agoTimeFormat)
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
            todayPrayerTimes = prayers.find { it.dateGregorian == timing.getTodayDate() }
            tomorrowPrayerTimes = prayers.find { it.dateGregorian == timing.getTomorrowDate() }

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
            interval = 100
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

    private fun updateUI() {
        updatePrayerTimesInScreen()
        scheduleRemainingTimeTillNextPrayerTime()
        listeners()
        startNewService(requireContext())
    }

    private fun run() {

        trackUserLocation()
        schedulePrayerTimesNotifications()
        observers()
        handleAutoStart()
        updateUI()
        updateWidget()
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

    private fun updatePrayerTimesInScreen() {
        todayPrayerTimes?.let {
            binding.prayerTimes = PrayerTimes(
                it.dateGregorian,
                it.dateHijri,
                it.monthHijri,
                timing.convertHmTo12HrsFormat(it.fajr),
                timing.convertHmTo12HrsFormat(it.sunrise),
                timing.convertHmTo12HrsFormat(it.dhuhur),
                timing.convertHmTo12HrsFormat(it.asr),
                timing.convertHmTo12HrsFormat(it.maghrib),
                timing.convertHmTo12HrsFormat(it.isha)
            )
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        timer.cancel()
    }

    private fun setRemainingTime() {

        todayPrayerTimes?.let {
            val prayerTimesStrings = mapOf(
                it.fajr to getString(R.string.fajr),
                it.dhuhur to getString(R.string.dhuhur),
                it.asr to getString(R.string.asr),
                it.maghrib to getString(R.string.maghrib),
                it.isha to getString(R.string.isha),
            )
            for (i in prayerTimesStrings) {
                val prayerTimeMillis =
                    timing.convertDmyHmToMillis("${timing.getTodayDate()} ${i.key}")
                val currentTimeMillis = System.currentTimeMillis()

                if (currentTimeMillis < prayerTimeMillis) {
                    val remainingTime = buildString {
                        append(getString(R.string.remainingTime))
                        append(" ")
                        append(i.value)
                    }
                    binding.remaining.text = remainingTime
                    val remainingTimeInMillis = prayerTimeMillis - currentTimeMillis

                    val remainingTimeString = timing.convertMillisToHMS(
                        remainingTimeInMillis,
                        "HH:mm:ss"
                    )

                    binding.remainingTime.text = remainingTimeString
                    return
                }
            }
        }

        binding.remaining.text = getString(R.string.time_remaining_to_fajr_prayer)

        tomorrowPrayerTimes?.let {
            val tomorrowFajrTimeMillis =
                timing.convertDmyHmToMillis("${timing.getTomorrowDate()} ${it.fajr}")
            val currentTimeMillis = System.currentTimeMillis()

            if (currentTimeMillis < tomorrowFajrTimeMillis) {
                val remainingTimeInMillis = tomorrowFajrTimeMillis - currentTimeMillis

                val remainingTimeString = timing.convertMillisToHMS(
                    remainingTimeInMillis,
                    "HH:mm:ss"
                )
                binding.remainingTime.text = remainingTimeString
            }
        }
    }
}