package com.ph.nabla_typemath

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity


class CustomCommand : AppCompatActivity() {

    private lateinit var linlin : LinearLayout
    private lateinit var commandText : TextView
    private lateinit var symbolText  : TextView

    @SuppressLint("InflateParams")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_custom_command)
        linlin = findViewById(R.id.custom_container_1)
        commandText  = findViewById(R.id.custom_command_1)
        symbolText   = findViewById(R.id.custom_symbol_1)

        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater

        // Pass null as the parent view because its going in the dialog layout
        val dialogView = inflater.inflate(R.layout.custom_command_dialog_layout, null)
        builder.setView(dialogView)
            .setCancelable(true)
            .setPositiveButton("DONE") { dialog, _ ->
                val commandInput : EditText = dialogView.findViewById(R.id.edit_command_dialog_command)
                commandText.text = commandInput.text
                dialog.cancel()
            }
            .setNeutralButton("DELETE"){ dialog, _ ->
                dialog.cancel()
            }
            .setNegativeButton("CANCEL"){ dialog, _ ->
                dialog.cancel()
            }

        val a = builder.create()
        linlin.setOnClickListener{
            a.show()
        }
    }
}