package com.ph.nabla_typemath

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity


class HelpActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_help)

        val versionLabel = findViewById<TextView>(R.id.nabla_version)
        val versionNumber = BuildConfig.VERSION_NAME
        val verStr = "version: $versionNumber"
        versionLabel.text = verStr

        val githubButton = findViewById<Button>(R.id.github_button)
        val githubURL    = "https://github.com/ray-pH/Nabla-TypeMath-android"
        githubButton.setOnClickListener {
            val i = Intent(Intent.ACTION_VIEW, Uri.parse(githubURL))
            startActivity(i)
        }

        val rateButton   = findViewById<Button>(R.id.rate_button)
        val rateURL      = "https://play.google.com/store/apps/details?id=com.ph.nabla_typemath"
        rateButton.setOnClickListener {
            val i = Intent(Intent.ACTION_VIEW, Uri.parse(rateURL))
            startActivity(i)
        }

        val donateButton = findViewById<Button>(R.id.donate_button)
        val donateURL    = "https://donorbox.org/support-nabla-typemath-development"
        donateButton.setOnClickListener {
            val i = Intent(Intent.ACTION_VIEW, Uri.parse(donateURL))
            startActivity(i)
        }
    }
}