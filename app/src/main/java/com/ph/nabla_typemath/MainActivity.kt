package com.ph.nabla_typemath

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.widget.Button

class MainActivity : AppCompatActivity() {

    // TODO: Add Help Page
    // TODO: Complete Setting Page
    // TODO: Custom Commands
    // TODO: App Blacklist and Whitelist

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val allowPermissionButton = findViewById<Button>(R.id.allowPermissionButton)
        allowPermissionButton.setOnClickListener {
            val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
            startActivity(intent)
        }

        val gotoSettingButton = findViewById<Button>(R.id.gotoSettingButton)
        gotoSettingButton.setOnClickListener {
            val intent = Intent(this, SettingActivity::class.java)
            startActivity(intent)
        }

        val symbolsButton = findViewById<Button>(R.id.symbolsButton)
        symbolsButton.setOnClickListener {
            val intent = Intent(this, SymbolsActivity::class.java)
            startActivity(intent)
        }
    }
}