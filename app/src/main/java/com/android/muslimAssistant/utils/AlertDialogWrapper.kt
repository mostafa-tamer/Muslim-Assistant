package com.android.muslimAssistant.utils

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import com.android.muslimAssistant.databinding.CustomAlertDialogBinding


class AlertDialogWrapper(
    var alertDialog: AlertDialog,
    var layout: CustomAlertDialogBinding
) {
    class Builder(context: Context) {
        private val alertDialog = AlertDialog.Builder(context).create()
        private var layout = CustomAlertDialogBinding.inflate(LayoutInflater.from(context))

        init {
            alertDialog.setView(layout.root)
            initializeVisibility()
            alertDialog.window?.setBackgroundDrawable(
                ColorDrawable(Color.TRANSPARENT)
            )
        }

        fun build(): AlertDialogWrapper {
            return AlertDialogWrapper(alertDialog, layout)
        }

        private fun initializeVisibility() {
            layout.image.visibility = View.GONE
            layout.titleText.visibility = View.GONE
            layout.messageText.visibility = View.GONE
            layout.body.visibility = View.GONE
            layout.cancelButton.visibility = View.GONE
            layout.okButton.visibility = View.GONE
        }

        fun setMessage(message: String): Builder {
            layout.messageText.visibility = View.VISIBLE
            layout.messageText.text = message
            return this
        }

        fun setTitle(title: String): Builder {
            layout.titleText.visibility = View.VISIBLE
            layout.titleText.text = title
            return this
        }

        fun setCancelable(isCancelable: Boolean): Builder {
            alertDialog.setCancelable(false)
            return this
        }

        fun showDialog() {
            alertDialog.show()
        }

        fun setPositiveButton(
            text: String, cancelable: Boolean = true, function: (Builder) -> Unit = {}
        ): Builder {
            layout.okButton.visibility = View.VISIBLE
//            layout.okButton.text = text
            layout.okButton.setOnClickListener {
                function(this)
                if (cancelable) this.dismiss()
            }
            return this
        }

        fun setNegativeButton(
            text: String, cancelable: Boolean = true, function: (Builder) -> Unit = {}
        ): Builder {
            layout.cancelButton.visibility = View.VISIBLE
//            layout.cancelButton.text = text
            layout.cancelButton.setOnClickListener {
                function(this)
                if (cancelable) this.dismiss()
            }
            return this
        }

        fun setOnDismiss(function: (Builder) -> Unit): Builder {
            alertDialog.setOnDismissListener {
                function(this)
            }
            return this
        }

        fun setBody(view: View): Builder {
            resetDialog()
            layout.body.visibility = View.VISIBLE
            layout.body.addView(view)
            return this
        }

        fun dismiss(): Builder {
            alertDialog.dismiss()
            return this
        }

        private fun resetDialog(): Builder {
            layout.body.removeAllViews()
            layout.body.visibility = View.GONE
            return this
        }
    }

}