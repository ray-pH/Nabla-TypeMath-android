package com.ph.typemath

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText

class SettingActivity : AppCompatActivity() {
    private val prefsName = "TypeMathPrefsFile"
    private lateinit var initCharEdit : EditText
    private lateinit var endStringEdit : EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)

        initCharEdit = findViewById(R.id.editTextInitialChar)
        endStringEdit = findViewById(R.id.editTextEndString)

        val saveSettingButton = findViewById<Button>(R.id.buttonSaveSetting)
        saveSettingButton.setOnClickListener {
            val sh: SharedPreferences = getSharedPreferences(prefsName, MODE_PRIVATE)
            val editor = sh.edit()
            editor.putString("initChar", initCharEdit.text.toString())
            editor.putString("endString", endStringEdit.text.toString())
            editor.apply()
        }
    }

    override fun onResume(){
        super.onResume()
        val sh : SharedPreferences = getSharedPreferences(prefsName, MODE_PRIVATE)
        val initChar : Char? = sh.getString("initChar", ".")?.get(0)
        val endString : String? = sh.getString("endString", ".-")

        initCharEdit.setText(initChar.toString())
        endStringEdit.setText(endString)
    }


}