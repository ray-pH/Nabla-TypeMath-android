package com.ph.nabla_typemath

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat


class SettingActivity : AppCompatActivity() {
    private val prefsName = "TypeMathPrefsFile"
    private lateinit var initStringEdit   : EditText
    private lateinit var endStringEdit    : EditText
    private lateinit var quickModeSwitch  : SwitchCompat
    private lateinit var latexModeSwitch  : SwitchCompat
    private lateinit var useSymbolSwitch  : SwitchCompat
    private lateinit var keepSpaceSwitch  : SwitchCompat
    private lateinit var diacriticsSwitch : SwitchCompat
    private lateinit var otherSettingLinear : LinearLayout
    private lateinit var themeSpinner     : Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)

        val customCommandButton = findViewById<Button>(R.id.custom_command_button)
        customCommandButton.setOnClickListener {
            val intent = Intent(this, CustomCommand::class.java)
            startActivity(intent)
        }

        val portCommandButton   = findViewById<Button>(R.id.custom_command_port_button)
        portCommandButton.setOnClickListener {
            val intent = Intent(this, CustomCommandPort::class.java)
            startActivity(intent)
        }

        initStringEdit     = findViewById(R.id.initialString_EditText)
        endStringEdit      = findViewById(R.id.endString_EditText)
        latexModeSwitch    = findViewById(R.id.latexMode_switch)
        quickModeSwitch    = findViewById(R.id.quickMode_switch)
        useSymbolSwitch    = findViewById(R.id.useAdditionalSymbol_switch)
        keepSpaceSwitch    = findViewById(R.id.keepSpace_switch)
        diacriticsSwitch   = findViewById(R.id.useDiacritics_switch)
        otherSettingLinear = findViewById(R.id.other_setting_linear)
        themeSpinner       = findViewById(R.id.theme_spinner)

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

        quickModeSwitch.setOnCheckedChangeListener{ _: CompoundButton, isChecked: Boolean ->
            editor.putBoolean("quickMode", isChecked)
            editor.apply()
            if(isChecked) latexModeSwitch.isChecked = false
        }
        latexModeSwitch.setOnCheckedChangeListener{ _: CompoundButton, isChecked: Boolean ->
            editor.putBoolean("latexMode", isChecked)
            editor.apply()
            if(isChecked) quickModeSwitch.isChecked = false
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
        diacriticsSwitch.setOnCheckedChangeListener{ _: CompoundButton, isChecked: Boolean ->
            editor.putBoolean("useDiacritics", isChecked)
            editor.apply()
        }

        ArrayAdapter.createFromResource(
            this, R.array.theme_array, android.R.layout.simple_spinner_item
        ).also{ adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            themeSpinner.adapter = adapter
        }

        val themeToast = Toast.makeText(applicationContext,
            "Restart the app for theme changes to take effect", Toast.LENGTH_SHORT)
        themeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            private var initialized : Boolean = false
            override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
                if(!initialized) initialized = true
                else {
                    editor.putInt("intTheme", pos)
                    editor.apply()
                    themeToast.show()
                }
            }
            override fun onNothingSelected(arg0: AdapterView<*>?) {
            }
        }
    }

    override fun onResume(){
        super.onResume()
        val sh : SharedPreferences = getSharedPreferences(prefsName, MODE_PRIVATE)
        val initStr          : String? = sh.getString("initString", ".")
        val endStr           : String? = sh.getString("endString", ".")
        val latexMode        : Boolean = sh.getBoolean("latexMode", false)
        val quickMode        : Boolean = sh.getBoolean("quickMode", true)
        val useAdditionalSym : Boolean = sh.getBoolean("useAdditionalSymbols", false)
        val keepSpace        : Boolean = sh.getBoolean("keepSpace", false)
        val useDiacritics    : Boolean = sh.getBoolean("useDiacritics", true)
        val intTheme         : Int     = sh.getInt("intTheme", 0)


        initStringEdit.setText(initStr)
        endStringEdit.setText(endStr)
        quickModeSwitch.isChecked  = quickMode
        latexModeSwitch.isChecked  = latexMode
        useSymbolSwitch.isChecked  = useAdditionalSym
        keepSpaceSwitch.isChecked  = keepSpace
        diacriticsSwitch.isChecked = useDiacritics
        themeSpinner.setSelection(intTheme)

        otherSettingLinear.visibility = if (latexMode) View.GONE else View.VISIBLE
    }


}