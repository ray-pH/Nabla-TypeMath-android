package com.ph.nabla_typemath

import android.content.ClipData
import android.content.ClipDescription.MIMETYPE_TEXT_PLAIN
import android.content.ClipboardManager
import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class CustomCommandPort : AppCompatActivity() {

    private val gsonHandler = CustomCommandGSONHandler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_custom_command_port)

        val prefsName = "TypeMathPrefsFile"
        val textBox = findViewById<EditText>(R.id.port_editText)
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

        val sh : SharedPreferences? = getSharedPreferences(prefsName, MODE_PRIVATE)
        val gSONStr = sh?.getString("customMap", "") ?: ""
        textBox.setText(gSONStr)

        val copiedToClipboard = Toast.makeText(applicationContext,
            "Copied to Clipboard", Toast.LENGTH_SHORT)
        val copyButton   = findViewById<Button>(R.id.port_copy_button)
        copyButton.setOnClickListener {
            val text = textBox.text
            val clip : ClipData = ClipData.newPlainText("simple text", text)
            clipboard.setPrimaryClip(clip)
            copiedToClipboard.show()
        }

        val pasteButton  = findViewById<Button>(R.id.port_paste_button)
        pasteButton.setOnClickListener {
            if(
                clipboard.hasPrimaryClip() &&
                clipboard.primaryClipDescription?.hasMimeType(MIMETYPE_TEXT_PLAIN) == true
            ){
                val pasteItem = clipboard.primaryClip?.getItemAt(0)
                val pasteData : String? = pasteItem?.text?.toString()
                if(pasteData != null) textBox.setText(pasteData)
            }
        }

        val donePortValid = Toast.makeText(applicationContext,
            "Import Successful", Toast.LENGTH_SHORT)
        val donePortInvalid = Toast.makeText(applicationContext,
            "Error : Input format is Invalid", Toast.LENGTH_SHORT)
        val doneButton  = findViewById<Button>(R.id.port_done_button)
        doneButton.setOnClickListener {
            val newGSONStr = textBox.text.toString()
            val isValid = gsonHandler.gSONValidMap(newGSONStr)
            if(isValid){
                val editor = sh?.edit()
                editor?.putString("customMap", newGSONStr)
                editor?.apply()
                if(editor != null) donePortValid.show()
            }else{
                donePortInvalid.show()
            }
        }
    }
}