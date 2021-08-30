package com.ph.nabla_typemath

import android.content.ClipData
import android.content.ClipDescription.MIMETYPE_TEXT_PLAIN
import android.content.ClipboardManager
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText

class CustomCommandPort : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_custom_command_port)

        val textBox = findViewById<EditText>(R.id.port_editText)
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

        val copyButton   = findViewById<Button>(R.id.port_copy_button)
        copyButton.setOnClickListener {
            val text = textBox.text
            val clip : ClipData = ClipData.newPlainText("simple text", text)
            clipboard.setPrimaryClip(clip)
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
    }
}