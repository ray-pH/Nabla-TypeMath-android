package com.ph.nabla_typemath

import android.content.DialogInterface
import android.os.Bundle
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity


class CustomCommand : AppCompatActivity() {

    private lateinit var linlin : LinearLayout

//    class FireMissilesDialogFragment : DialogFragment() {
//        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
//            return activity?.let {
//                // Use the Builder class for convenient dialog construction
//                val builder = AlertDialog.Builder(it)
//                builder.setMessage("test popup")
//                    .setPositiveButton("nani kore",
//                        DialogInterface.OnClickListener { dialog, id ->
//                            // FIRE ZE MISSILES!
//                        })
//                    .setNegativeButton("neg",
//                        DialogInterface.OnClickListener { dialog, id ->
//                            // User cancelled the dialog
//                        })
//                // Create the AlertDialog object and return it
//                builder.create()
//            } ?: throw IllegalStateException("Activity cannot be null")
//        }
//    }

//    private val fdial = FireMissilesDialogFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_custom_command)
        linlin = findViewById(R.id.custom_container_1)
        val b1 = AlertDialog.Builder(this)
        b1.setMessage("mesej")
        b1.setCancelable(true)
        b1.setPositiveButton(
            "Yes"
        ) { dialog, id -> dialog.cancel() }
        b1.setNegativeButton(
            "No"
        ) { dialog, id -> dialog.cancel() }
        val a = b1.create()

        linlin.setOnClickListener{
            a.show()
        }
    }
}