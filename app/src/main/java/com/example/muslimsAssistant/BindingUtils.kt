package com.example.muslimsAssistant

import android.widget.TextView
import androidx.databinding.BindingAdapter


@BindingAdapter("prayers")
fun prayers(textView: TextView, value: String?) {
    value?.let {
        val hrs = value.substring(0, 2).toInt()
        var time = if (hrs > 12)
            (value.substring(0, 2).toInt() - 12).toString()
        else
            (value.substring(0, 2).toInt()).toString()

        time += value.substring(2)

        val amPm: String = if (value.substring(0, 2).toInt() < 12)
            "صباحاً"
        else
            "مساءً"
        val text = "$time $amPm"

        when (textView.hint) {
            "fajr" -> {
                val fajrText = "الفجر: $text"
                textView.text = fajrText
            }
            "sunrise" -> {
                val sunriseText = "الشروق: $text"
                textView.text = sunriseText
            }
            "duhr" -> {
                val duhurText = "الظهر: $text"
                textView.text = duhurText
            }
            "asr" -> {
                val asrText = "العصر: $text"
                textView.text = asrText
            }
            "magrib" -> {
                val magribText = "المغرب: $text"
                textView.text = magribText
            }
            "isha" -> {
                val ishaText = "العشاء: $text"
                textView.text = ishaText
            }
        }
    }
}