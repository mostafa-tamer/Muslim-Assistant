package com.example.muslimsAssistant.utils

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import com.example.muslimsAssistant.databinding.CustomAlertDialogBinding


class CustomAlertDialog(context: Context) {
    private val alertDialog = AlertDialog.Builder(context).create()
    private var layout = CustomAlertDialogBinding.inflate(LayoutInflater.from(context))

    init {
        alertDialog.setView(layout.root)
        initializeVisibility()
        alertDialog.window?.setBackgroundDrawable(
            ColorDrawable(Color.TRANSPARENT)
        )
    }

    private fun initializeVisibility() {
        layout.image.visibility = View.GONE
        layout.titleText.visibility = View.GONE
        layout.messageText.visibility = View.GONE
        layout.body.visibility = View.GONE
        layout.cancelButton.visibility = View.GONE
        layout.okButton.visibility = View.GONE
    }

    fun setMessage(message: String): CustomAlertDialog {
        layout.messageText.visibility = View.VISIBLE
        layout.messageText.text = message
        return this
    }

    fun setTitle(title: String): CustomAlertDialog {
        layout.titleText.visibility = View.VISIBLE
        layout.titleText.text = title
        return this
    }

    fun setCancelable(isCancelable: Boolean): CustomAlertDialog {
        alertDialog.setCancelable(false)
        return this
    }

    fun showDialog() {
        alertDialog.show()
    }

    fun setPositiveButton(
        text: String, cancelable: Boolean = true, function: (CustomAlertDialog) -> Unit = {}
    ): CustomAlertDialog {
        layout.okButton.visibility = View.VISIBLE
        layout.okButton.text = text
        layout.okButton.setOnClickListener {
            function(this)
            if (cancelable) this.dismiss()
        }
        return this
    }

    fun setNegativeButton(
        text: String, cancelable: Boolean = true, function: (CustomAlertDialog) -> Unit = {}
    ): CustomAlertDialog {
        layout.cancelButton.visibility = View.VISIBLE
        layout.cancelButton.text = text
        layout.cancelButton.setOnClickListener {
            function(this)
            if (cancelable) this.dismiss()
        }
        return this
    }

    fun setOnDismiss(function: (CustomAlertDialog) -> Unit): CustomAlertDialog {
        alertDialog.setOnDismissListener {
            function(this)
        }
        return this
    }

    fun setBody(view: View): CustomAlertDialog {
        resetDialog()
        layout.body.visibility = View.VISIBLE
        layout.body.addView(view)
        return this
    }

    fun dismiss(): CustomAlertDialog {
        alertDialog.dismiss()
        return this
    }

    private fun resetDialog(): CustomAlertDialog {
        layout.body.removeAllViews()
        layout.body.visibility = View.GONE
        return this
    }
}