package com.ph.nabla_typemath

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity


class CustomCommand : AppCompatActivity() {

    private val tag = "NablaMathTypeService"
    private val gsonHandler = CustomCommandGSONHandler()
    private var counter = 0
    private var maxIndex = 0
    private val prefsName = "TypeMathPrefsFile"
    private lateinit var verticalLayout: LinearLayout

    private val customSymbolMap : LinkedHashMap<String, String> = linkedMapOf(
        "aa" to "11",
        "bb" to "22",
        "cc" to "33",
    )

    // Generate LinkedHashMap from editor
    private fun genLinkedHashMap(): LinkedHashMap<String,String>{
        val linkedMap = LinkedHashMap<String,String>()
        for (genCounter in 0..this.maxIndex){
            val linearLayout : LinearLayout = verticalLayout.findViewWithTag(
                "custom_container_${genCounter}") ?: continue
            val commandText: TextView = linearLayout.findViewWithTag(
                "custom_command_${genCounter}")   ?: continue
            val symbolText : TextView = linearLayout.findViewWithTag(
                "custom_symbol_${genCounter}")    ?: continue
            val commandStr = commandText.text.toString()
            val symbolStr  = symbolText.text.toString()
            linkedMap[commandStr] = symbolStr
        }
        return linkedMap
    }

    // prepare counter until reached available value
    private fun prepareCounter(){
        var available = false
        do{
            val linearLayout : LinearLayout? = verticalLayout.findViewWithTag(
                "custom_container_${this.counter}")
            if(linearLayout == null) available = true
            else this.counter++
        } while(!available)
    }

    // set when a linearLayout(horizontal) is Clicker, an editor dialog pop up
    private fun setOnClickAlertDialog(n: Int){
        val linearLayout : LinearLayout? = verticalLayout.findViewWithTag("custom_container_${n}")
        val commandText  : TextView?     = linearLayout?.findViewWithTag("custom_command_${n}")
        val symbolText   : TextView?     = linearLayout?.findViewWithTag("custom_symbol_${n}")

        val builder  = AlertDialog.Builder(this)
        val inflater = layoutInflater

        val dialogView = inflater.inflate(R.layout.custom_command_dialog_layout, null)
        builder.setView(dialogView)
            .setCancelable(true)
            .setPositiveButton("DONE") { dialog, _ ->
                val commandInput : EditText =
                    dialogView.findViewById(R.id.edit_command_dialog_command)
                val symbolInput  : EditText =
                    dialogView.findViewById(R.id.edit_command_dialog_symbol)
                commandText?.text = commandInput.text
                symbolText?.text  = symbolInput.text
                dialog.cancel()
            }
            .setNeutralButton("DELETE"){ dialog, _ ->
                dialog.cancel()
            }
            .setNegativeButton("CANCEL"){ dialog, _ -> dialog.cancel() }
            .setTitle("Edit Custom Command")

        val alertDialog = builder.create()
        linearLayout?.setOnClickListener{
            val commandInput : EditText =
                dialogView.findViewById(R.id.edit_command_dialog_command)
            val symbolInput  : EditText =
                dialogView.findViewById(R.id.edit_command_dialog_symbol)
            commandInput.setText(commandText?.text, TextView.BufferType.EDITABLE)
            symbolInput.setText(symbolText?.text, TextView.BufferType.EDITABLE)
            alertDialog.show()
        }
    }

    private val textLayoutParams : LinearLayout.LayoutParams = LinearLayout.LayoutParams(
        LinearLayout.LayoutParams.WRAP_CONTENT,
        LinearLayout.LayoutParams.WRAP_CONTENT,
        1.0f,
    )
    // add new row to customCommand layout
    @Suppress("SameParameterValue")
    private fun addNewCommand(verticalLayout: LinearLayout?,
                              textCommandStr: String,
                              textSymbolStr : String,
                              doPrepareCounter : Boolean = true,
    ){
        if(doPrepareCounter) prepareCounter()

        val textCommand  = TextView(this)
        textCommand.text = textCommandStr
        textCommand.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16f)
        textCommand.tag  = "custom_command_${this.counter}"
        textCommand.layoutParams = textLayoutParams

        val textSymbol   = TextView(this)
        textSymbol.text = textSymbolStr
        textSymbol.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16f)
        textSymbol.tag  = "custom_symbol_${this.counter}"
        textSymbol.textAlignment = TextView.TEXT_ALIGNMENT_VIEW_END
        textSymbol.layoutParams  = textLayoutParams

        val horizontalLayout = LinearLayout(this)
        horizontalLayout.orientation = LinearLayout.HORIZONTAL
        horizontalLayout.isClickable = true
        horizontalLayout.isFocusable = true
        horizontalLayout.tag = "custom_container_${this.counter}"
        horizontalLayout.addView(textCommand)
        horizontalLayout.addView(textSymbol)

        val horizontalLayoutParam = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        val pxOf16dp  = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, 16f, resources.displayMetrics
        ).toInt()
        horizontalLayoutParam.setMargins(0,0,0,pxOf16dp)
        verticalLayout?.addView(horizontalLayout, horizontalLayoutParam)

        setOnClickAlertDialog(this.counter)
        if (this.counter > this.maxIndex) this.maxIndex = this.counter
        this.counter++
    }

    // Add new empty custom command
    private fun addNewEmptyCommand() {
        prepareCounter()
        val n = this.counter
        addNewCommand(verticalLayout, "command$n","command$n"
            , false)
    }

    // Populate layout with custom commands from map
    private fun populateWithMap(customMap: LinkedHashMap<String, String>){
        prepareCounter()
        customMap.forEach{ (key,value) ->
            addNewCommand(verticalLayout, key, value, false)
        }
        this.maxIndex = this.counter-1
    }


    @SuppressLint("InflateParams")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_custom_command)
        verticalLayout = findViewById(R.id.custom_command_layout)
        val addButton : Button = findViewById(R.id.add_custom_command_button)

        // DEBUG
        val saveButton : Button = findViewById(R.id.save_button)
        saveButton.setOnClickListener {
            val linkedMap = genLinkedHashMap()
            val gSONStr = gsonHandler.linkedMapToGSONStr(linkedMap)
            Log.i(tag, gSONStr)
            val sh : SharedPreferences = getSharedPreferences(prefsName, MODE_PRIVATE)
            val editor = sh.edit()
            editor.putString("customMap", gSONStr)
            editor.apply()
        }
        // DEBUG

        addButton.setOnClickListener {
            addNewEmptyCommand()
        }

        counter = 1
        setOnClickAlertDialog(counter)

        val sh : SharedPreferences = getSharedPreferences(prefsName, MODE_PRIVATE)
        val gSONStr = sh.getString("customMap", "") ?: ""
        if(gSONStr.isNotEmpty()){
            val customMap : LinkedHashMap<String,String> =
                gsonHandler.gSONStrToLinkedMap(gSONStr)
            populateWithMap(customMap)
        }
//        populateWithMap(customSymbolMap)
    }
}