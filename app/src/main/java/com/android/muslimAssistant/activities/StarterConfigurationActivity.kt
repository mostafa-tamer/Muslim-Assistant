package com.android.muslimAssistant.activities

import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.android.muslimAssistant.R
import com.android.muslimAssistant.databinding.ActivityStarterConfigurationBinding
import com.android.muslimAssistant.databinding.MethodViewHolderBinding
import com.android.muslimAssistant.repository.SharedPreferencesRepository
import com.android.muslimAssistant.utils.Wrapper
import com.android.muslimAssistant.utils.methodsArabic
import com.android.muslimAssistant.utils.methodsEnglish
import com.android.muslimAssistant.utils.toast
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.koin.java.KoinJavaComponent
import java.util.*

class StarterConfigurationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStarterConfigurationBinding
    private val methodsListWrapper by lazy { Wrapper<List<String>>(methodsEnglish) }

    private val sharedPreferencesRepository by lazy { SharedPreferencesRepository(this) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        run()
    }

    private fun run() {
        binding = ActivityStarterConfigurationBinding.inflate(layoutInflater)

        methodsListInitializer()
        handelAppLanguage()
        handleOrientationBasedOnLanguage()
        handleMethodButtonClickListener()
        setContentView(binding.root)
    }

    private fun handelAppLanguage() {
        val repository =
            KoinJavaComponent.get<SharedPreferencesRepository>(SharedPreferencesRepository::class.java)
        lifecycleScope.launch {
            val language: String = repository.language.first()
            val locale = Locale(language)
            val resources = resources
            val configuration = resources.configuration
            configuration.setLocale(locale)
            val updatedResources = Resources(assets, resources.displayMetrics, configuration)
            resources.updateConfiguration(configuration, updatedResources.displayMetrics)

            binding =
                ActivityStarterConfigurationBinding.inflate(layoutInflater)

            if (language == "ar") {
                window.decorView.layoutDirection = View.LAYOUT_DIRECTION_RTL
                binding.arabicRadio.isChecked = true
            } else {
                window.decorView.layoutDirection = View.LAYOUT_DIRECTION_LTR
                binding.englishRadio.isChecked = true
            }
        }
    }

    private fun startMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        finish()
    }

    private fun handleMethodButtonClickListener() {
        val method = handleMethod()
        binding.nextActionButton.setOnClickListener {
            if (method.value != -1) {
                lifecycleScope.launch {
                    sharedPreferencesRepository.updateMethod(method.value.toString())

                    sharedPreferencesRepository.updateLanguageSelection(handleLanguage())
                    startMainActivity()
                }
            } else {
                toast?.cancel()
                toast = Toast.makeText(
                    this,
                    "Please select a method!",
                    Toast.LENGTH_SHORT
                )
                toast?.show()
            }
        }
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

    private fun handleOrientationBasedOnLanguage() {
        binding.radioGroup.setOnCheckedChangeListener { _, _ ->
            lifecycleScope.launch {
                sharedPreferencesRepository.updateLanguageSelection(handleLanguage())
                handelAppLanguage()
                run()
            }
        }
    }

    private fun handleMethod(): Wrapper<Int> {
        val method = Wrapper<Int>(-1)

        runBlocking {
            if (sharedPreferencesRepository.method.first() != "-1") {
                startMainActivity()
            }
        }

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
                    method.value = methodsListWrapper.value.indexOf(i)
                }
            }

            binding.container.addView(methodViewHolderBinding.root)
        }
        return method
    }

    private fun handleLanguage(): String {
        return when (binding.radioGroup.checkedRadioButtonId) {
            R.id.english_radio -> "en"
            else -> "ar"
        }
    }
}