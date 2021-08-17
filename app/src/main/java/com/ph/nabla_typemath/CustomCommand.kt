package com.ph.nabla_typemath

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.TypedValue
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity


class CustomCommand : AppCompatActivity() {

    private var counter = 0

    private fun setOnClickAlertDialog(verticalLayout: LinearLayout?, n: Int){
        val linearLayout : LinearLayout? = verticalLayout?.findViewWithTag("custom_container_${n}")
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
    private fun addNewCommand(verticalLayout: LinearLayout?, n: Int){
        val textCommand  = TextView(this)
        val textCommandStr = "command$n"
        textCommand.text = textCommandStr
        textCommand.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16f)
        textCommand.tag  = "custom_command_${n}"
        textCommand.layoutParams = textLayoutParams

        val textSymbol   = TextView(this)
        val textSymbolStr = "symbol$n"
        textSymbol.text = textSymbolStr
        textSymbol.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16f)
        textSymbol.tag  = "custom_symbol_${n}"
        textSymbol.textAlignment = TextView.TEXT_ALIGNMENT_VIEW_END
        textSymbol.layoutParams  = textLayoutParams

        val horizontalLayout = LinearLayout(this)
        horizontalLayout.orientation = LinearLayout.HORIZONTAL
        horizontalLayout.isClickable = true
        horizontalLayout.isFocusable = true
        horizontalLayout.tag = "custom_container_${n}"
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

        setOnClickAlertDialog(verticalLayout, n)
    }

    @SuppressLint("InflateParams")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_custom_command)
        val customCommandLayout : LinearLayout? = findViewById(R.id.custom_command_layout)
        val addButton : Button = findViewById(R.id.add_custom_command_button)

        addButton.setOnClickListener {
            addNewCommand(customCommandLayout, this.counter++)
        }

        counter = 1
        setOnClickAlertDialog(customCommandLayout, counter++)
        addNewCommand(customCommandLayout, counter++)


    }
}