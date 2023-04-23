package com.android.muslimAssistant.activities

import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.android.muslimAssistant.R
import com.android.muslimAssistant.databinding.ActivityConfigurationBinding
import com.android.muslimAssistant.databinding.MethodViewHolderBinding
import com.android.muslimAssistant.repository.SharedPreferencesRepository
import com.android.muslimAssistant.repository.TasbeehFragmentRepository
import com.android.muslimAssistant.utils.Wrapper
import com.android.muslimAssistant.utils.methodsArabic
import com.android.muslimAssistant.utils.methodsEnglish
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.koin.java.KoinJavaComponent
import java.util.*

class ConfigurationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityConfigurationBinding
    private val methodsListWrapper by lazy { Wrapper<List<String>>(methodsEnglish) }
    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            startActivity(Intent(this@ConfigurationActivity, MainActivity::class.java))
            finish()
        }
    }

    private val sharedPreferencesRepository: SharedPreferencesRepository by lazy {
        SharedPreferencesRepository(this)
    }

    private val tasbeehFragmentRepository: TasbeehFragmentRepository by lazy {
        TasbeehFragmentRepository(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        run()
    }

    private fun onBackPressedCallback() {
        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
    }

    private fun run() {
        binding =
            ActivityConfigurationBinding.inflate(layoutInflater)

        handelAppLanguage()
        handleNotificationSwitch()
        handleLanguageRadioSelection()
        methodsListInitializer()
        handleMethod()
        handleDhikr()
        onBackPressedCallback()
        setContentView(binding.root)
        handleBackButton()
    }

    private fun handleDhikr() {
        binding.highestDhikrText.text = runBlocking {
            buildString {
                append(getString(R.string.highest_dhikr))
                append(" ")
                append(tasbeehFragmentRepository.highestValue.first())
            }
        }

        binding.indicatorEdit.setText(
            runBlocking {
                tasbeehFragmentRepository.indicatorMax.first().toString()
            }
        )
        binding.resetHighestDhikr.setOnClickListener {
            lifecycleScope.launch {
                tasbeehFragmentRepository.resetHighestValue()
                tasbeehFragmentRepository.resetCounter()
                val restedHighestScore = getString(R.string.highest_dhikr) + " 0"
                binding.highestDhikrText.text = restedHighestScore
            }
        }

        binding.indicatorEdit.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(p0: Editable?) {
                runBlocking {
                    tasbeehFragmentRepository.updateIndicatorMax(
                        if (p0.toString().isNotEmpty()) {
                            p0.toString().toInt()
                        } else {
                            0
                        }
                    )
                }
            }
        })
    }

    private fun methodsListInitializer() {
        methodsListWrapper.value = runBlocking {
            if (sharedPreferencesRepository.language.first() == "ar") {
                methodsArabic
            } else {
                methodsEnglish
            }
        }
    }

    private fun handleBackButton() {
        binding.toolbar.backButton.visibility = View.VISIBLE
        binding.toolbar.backButton.setOnClickListener {
            onBackPressedCallback.handleOnBackPressed()
        }
    }

    private fun handleNotificationSwitch() {
        lifecycleScope.launch {
            if (sharedPreferencesRepository.isNotification.first())
                binding.notificationSwitch.isChecked = true
        }

        binding.notificationSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                lifecycleScope.launch {
                    sharedPreferencesRepository.isNotification(true)
                }
            } else {
                lifecycleScope.launch {
                    sharedPreferencesRepository.isNotification(false)
                }
            }
        }
    }

    private fun handleLanguageRadioSelection() {
        binding.radioGroup.setOnCheckedChangeListener { _, _ ->
            val language = handleLanguage()
            lifecycleScope.launch {
                if (language == "ar") {
                    sharedPreferencesRepository.updateLanguageSelection(language)
                    methodsListWrapper.value = methodsArabic
                } else {
                    sharedPreferencesRepository.updateLanguageSelection(language)
                    methodsListWrapper.value = methodsEnglish
                }
                run()
            }
        }
    }

    private fun handelAppLanguage() {
        val repository =
            KoinJavaComponent.get<SharedPreferencesRepository>(SharedPreferencesRepository::class.java)
        runBlocking {
            val language: String = repository.language.first()

            val locale = Locale(language)
            val resources = resources
            val configuration = resources.configuration
            configuration.setLocale(locale)
            val updatedResources = Resources(assets, resources.displayMetrics, configuration)
            resources.updateConfiguration(configuration, updatedResources.displayMetrics)

            binding =
                ActivityConfigurationBinding.inflate(layoutInflater)

            if (language == "ar") {
                window.decorView.layoutDirection = View.LAYOUT_DIRECTION_RTL
                binding.arabicRadio.isChecked = true
            } else {
                window.decorView.layoutDirection = View.LAYOUT_DIRECTION_LTR
                binding.englishRadio.isChecked = true
            }
        }
    }

    private fun handleMethod() {
        var lastChild: TextView? = null

        for (i in methodsListWrapper.value) {
            val methodViewHolderBinding = MethodViewHolderBinding.inflate(layoutInflater)
            methodViewHolderBinding.methodTextView.apply {
                text = i
                setOnClickListener {
                    lifecycleScope.launch {
                        lastChild?.setBackgroundColor(
                            ContextCompat.getColor(
                                applicationContext,
                                R.color.blue
                            )
                        )
                        lastChild?.setTextColor(
                            ContextCompat.getColor(
                                applicationContext,
                                R.color.white
                            )
                        )

                        setBackgroundColor(
                            ContextCompat.getColor(
                                applicationContext,
                                R.color.white
                            )
                        )
                        setTextColor(
                            ContextCompat.getColor(
                                applicationContext,
                                R.color.black
                            )
                        )
                        lastChild = this@apply

                    }
                    lifecycleScope.launch {
                        val selectedMethod = methodsListWrapper.value.indexOf(i).toString()
                        sharedPreferencesRepository.updateMethod(selectedMethod)
                        println(handleLanguage())
                        sharedPreferencesRepository.updateLanguageSelection(handleLanguage())
                    }
                }
            }

            binding.container.addView(methodViewHolderBinding.root)
        }

        lifecycleScope.launch {
            val method = sharedPreferencesRepository.method.first()
            if (method != "-1") {

                val button = ((binding.container.getChildAt(method.toInt()) as LinearLayout)
                    .getChildAt(0) as com.balysv.materialripple.MaterialRippleLayout)
                    .getChildView<Button>()

                button.setBackgroundColor(
                    ContextCompat.getColor(
                        applicationContext,
                        R.color.white
                    )
                )

                button.setTextColor(
                    ContextCompat.getColor(
                        applicationContext,
                        R.color.black
                    )
                )
                lastChild = button
            }
        }
    }

    private fun handleLanguage(): String {
        return when (binding.radioGroup.checkedRadioButtonId) {
            R.id.english_radio -> "en"
            else -> "ar"
        }
    }
}


