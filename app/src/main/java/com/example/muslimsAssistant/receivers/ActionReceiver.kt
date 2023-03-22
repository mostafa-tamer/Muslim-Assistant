package com.example.muslimsAssistant.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationManagerCompat

class ActionReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        println("Action Receiver")
        NotificationManagerCompat.from(context).cancelAll()
    }
}