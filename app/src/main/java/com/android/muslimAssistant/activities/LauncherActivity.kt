package com.android.muslimAssistant.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.android.muslimAssistant.R
import java.util.*

class LauncherActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_luncher)

        Timer().schedule(
            object : TimerTask() {
                override fun run() {
                    startActivity(Intent(this@LauncherActivity, StarterConfigurationActivity::class.java))
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
                    finish()
                }
            }, 400
        )
    }
}
