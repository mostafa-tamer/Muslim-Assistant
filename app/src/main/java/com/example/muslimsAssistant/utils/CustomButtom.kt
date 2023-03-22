package com.example.muslimsAssistant.utils

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageButton


class CustomButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatButton(context, attrs, defStyleAttr) {

    var lock = false

    override fun setOnClickListener(l: OnClickListener?) {

        super.setOnClickListener {
            if (!lock) {
                l?.onClick(this)
            }
        }
    }

    fun lockButton() {
        lock = (true)
    }

    fun unlockButton() {
        lock = (false)
    }
}

class CustomImageButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatImageButton(context, attrs, defStyleAttr) {

    private var lock = false

    override fun setOnClickListener(l: OnClickListener?) {

        super.setOnClickListener {
            if (!lock) {
                l?.onClick(this)
            }
        }
    }

    fun lockButton() {
        lock = true
    }

    fun unlockButton() {
        lock = false
    }
}