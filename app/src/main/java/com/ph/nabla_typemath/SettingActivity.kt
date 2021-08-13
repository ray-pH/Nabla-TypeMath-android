package com.ph.nabla_typemath

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.widget.SwitchCompat

class SettingActivity : AppCompatActivity() {
    private val prefsName = "TypeMathPrefsFile"
    private lateinit var initStringEdit  : EditText
    private lateinit var endStringEdit   : EditText
    private lateinit var latexModeSwitch : SwitchCompat
    private lateinit var useSymbolSwitch : SwitchCompat
    private lateinit var keepSpaceSwitch : SwitchCompat
    private lateinit var otherSettingLinear : LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)

        initStringEdit     = findViewById(R.id.initialString_EditText)
        endStringEdit      = findViewById(R.id.endString_EditText)
        latexModeSwitch    = findViewById(R.id.latexMode_switch)
        useSymbolSwitch    = findViewById(R.id.useAdditionalSymbol_switch)
        keepSpaceSwitch    = findViewById(R.id.keepSpace_switch)
        otherSettingLinear = findViewById(R.id.other_setting_linear)

        val stringSaveToast = Toast.makeText(applicationContext,
            "String Saved", Toast.LENGTH_SHORT)
        val sh: SharedPreferences = getSharedPreferences(prefsName, MODE_PRIVATE)
        val editor = sh.edit()

        val saveStringButton = findViewById<Button>(R.id.buttonSaveString)
        saveStringButton.setOnClickListener {
            editor.putString("initString", initStringEdit.text.toString())
            editor.putString("endString" , endStringEdit.text.toString())
            editor.apply()
            stringSaveToast.show()
        }

        latexModeSwitch.setOnCheckedChangeListener{ _: CompoundButton, isChecked: Boolean ->
            editor.putBoolean("latexMode", isChecked)
            editor.apply()
            otherSettingLinear.visibility = if(isChecked) View.GONE else View.VISIBLE
        }
        useSymbolSwitch.setOnCheckedChangeListener{ _: CompoundButton, isChecked: Boolean ->
            editor.putBoolean("useAdditionalSymbols", isChecked)
            editor.apply()
        }
        keepSpaceSwitch.setOnCheckedChangeListener{ _: CompoundButton, isChecked: Boolean ->
            editor.putBoolean("keepSpace", isChecked)
            editor.apply()
        }

    }

    override fun onResume(){
        super.onResume()
        val sh : SharedPreferences = getSharedPreferences(prefsName, MODE_PRIVATE)
        val initStr          : String? = sh.getString("initString", ".")
        val endStr           : String? = sh.getString("endString", ".")
        val latexMode        : Boolean = sh.getBoolean("latexMode", false)
        val useAdditionalSym : Boolean = sh.getBoolean("useAdditionalSymbols", false)
        val keepSpace        : Boolean = sh.getBoolean("keepSpace", false)


        initStringEdit.setText(initStr)
        endStringEdit.setText(endStr)
        latexModeSwitch.isChecked = latexMode
        useSymbolSwitch.isChecked = useAdditionalSym
        keepSpaceSwitch.isChecked = keepSpace

        otherSettingLinear.visibility = if (latexMode) View.GONE else View.VISIBLE
    }


}