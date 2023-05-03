package com.android.muslimAssistant.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.android.muslimAssistant.utils.requestCodeGenerator

class WidgetReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        println(requestCodeGenerator())
        val text = intent.getStringExtra("value")
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
    }
}
