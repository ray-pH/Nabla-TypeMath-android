package com.ph.nabla_typemath

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Button
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    // TODO: Add Help Page
    // TODO: Complete Setting Page
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

    private fun isAccessServiceEnabled(context: Context): Boolean {
        val serviceName = "MathTypeService"
        val prefString =
            Settings.Secure.getString(context.contentResolver, Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES)
        return prefString.contains("${context.packageName}/${context.packageName}.${serviceName}")
    }

    override fun onResume(){
        super.onResume()
        val serviceEnabled  = isAccessServiceEnabled(applicationContext)
        val serviceInfoOn   = findViewById<TextView>(R.id.service_info_label_on)
        val serviceInfoOff  = findViewById<TextView>(R.id.service_info_label_off)
        val serviceInfoDesc = findViewById<TextView>(R.id.service_allow_permission_desc)

        if(serviceEnabled){
            serviceInfoOn.visibility   = View.VISIBLE
            serviceInfoOff.visibility  = View.GONE
            serviceInfoDesc.visibility = View.GONE
        }else{
            serviceInfoOn.visibility   = View.GONE
            serviceInfoOff.visibility  = View.VISIBLE
            serviceInfoDesc.visibility = View.VISIBLE
        }
    }
}