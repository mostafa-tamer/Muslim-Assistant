package com.example.muslimsAssistant.utils

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import com.example.barcodeReader.utils.ErrorMessage
import com.example.muslimsAssistant.database.ReminderItem
import com.example.muslimsAssistant.receivers.ReminderReceiver


fun cancelPendingIntent(code: Int, context: Context) {
    val intent = Intent(context, ReminderReceiver::class.java)
    val pendingIntent: PendingIntent =
        PendingIntent.getBroadcast(
            context,
            code,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

    (context.getSystemService(Context.ALARM_SERVICE) as AlarmManager)
        .cancel(
            pendingIntent
        )
}

fun alertDialogErrorMessageObserver(
    errorMessageLiveData: MutableLiveData<ErrorMessage>,
    viewLifecycleOwner: LifecycleOwner,
    exceptionErrorMessageAlertDialog: CustomAlertDialog
) {
    errorMessageLiveData.observe(viewLifecycleOwner) {
        if (it.errorExist) {
            exceptionErrorMessageAlertDialog.setTitle(it.title).setMessage(it.message)
                .setPositiveButton("Ok").showDialog()
            errorMessageLiveData.value = ErrorMessage()
        }
    }
}

fun toastErrorMessageObserver(
    errorMessageLiveData: MutableLiveData<ErrorMessage>,
    viewLifecycleOwner: LifecycleOwner,
    context: Context
) {
    errorMessageLiveData.observe(viewLifecycleOwner) {
        if (it.errorExist) {
            Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
            errorMessageLiveData.value = ErrorMessage()
        }
    }
}