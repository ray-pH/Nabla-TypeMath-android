package com.ph.nabla_typemath

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Button
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.bundleOf

class MainActivity : AppCompatActivity() {


    // TODO: App Blacklist and Whitelist
    private fun setTheme(){
        val prefsName = "TypeMathPrefsFile"
        val sh: SharedPreferences? = getSharedPreferences(prefsName, MODE_PRIVATE)
        AppCompatDelegate.setDefaultNightMode(
            if(sh != null){
                when(sh.getInt("intTheme", 0)){
                    0 -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                    1 -> AppCompatDelegate.MODE_NIGHT_YES
                    2 -> AppCompatDelegate.MODE_NIGHT_NO
                    else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                }
            }else AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val allowPermissionButton = findViewById<Button>(R.id.allowPermissionButton)
        allowPermissionButton.setOnClickListener {
            this.acessServiceDialogAlert(applicationContext)
        }

        val gotoSettingButton = findViewById<Button>(R.id.gotoSettingButton)
        gotoSettingButton.setOnClickListener {
            val intent = Intent(this, SettingActivity::class.java)
            startActivity(intent)
        }

        val helpButton = findViewById<Button>(R.id.helpButton)
        helpButton.setOnClickListener {
            val intent = Intent(this, HelpActivity::class.java)
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
        val prefString : String? =
            Settings.Secure.getString(context.contentResolver, Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES)
        val serviceStr = "${context.packageName}/${context.packageName}.${serviceName}"
        return prefString?.contains(serviceStr) ?: false
    }

    private fun acessServiceDialogAlert(context: Context){
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle(R.string.accessalert_title)
        builder.setMessage(R.string.service_allow_permission_desc)
        builder.setPositiveButton(R.string.accessalert_pos
        ) { _: DialogInterface?, _: Int ->
            val serviceName = "MathTypeService"
            val argKey   = ":settings:fragment_args_key"
            val serviceStr = "${context.packageName}/${context.packageName}.${serviceName}"
            val showArgs = serviceStr
            // val showArgs = packageName + "/" + T::class.java.name

            val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK

            intent.putExtra(":settings:fragment_args_key", showArgs)
            intent.putExtra(":settings:show_fragment_args", bundleOf(argKey to showArgs))
            startActivity(intent)
        }
        builder.setNegativeButton(R.string.accessalert_neg
        ) { dialog: DialogInterface, _: Int ->
            dialog.cancel()
        }
        builder.show()
    }

    override fun onResume(){
        super.onResume()
        val serviceEnabled  = isAccessServiceEnabled(applicationContext)
        val serviceInfoOn   = findViewById<TextView>(R.id.service_info_label_on)
        val serviceInfoOff  = findViewById<TextView>(R.id.service_info_label_off)
        val serviceInfoDesc = findViewById<TextView>(R.id.service_allow_permission_desc)
        val serviceInfoFrame = findViewById<ScrollView>(R.id.service_info_frame)

        if(serviceEnabled){
            serviceInfoOn.visibility   = View.VISIBLE
            serviceInfoOff.visibility  = View.GONE
            serviceInfoDesc.visibility = View.GONE
            serviceInfoFrame.visibility = View.GONE
        }else{
            serviceInfoOn.visibility   = View.GONE
            serviceInfoOff.visibility  = View.VISIBLE
            serviceInfoDesc.visibility = View.VISIBLE
            serviceInfoFrame.visibility = View.VISIBLE
            this.acessServiceDialogAlert(applicationContext)
        }
    }
}