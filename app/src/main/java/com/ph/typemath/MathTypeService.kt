package com.ph.typemath

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.widget.EditText


class MathTypeService : AccessibilityService() {
    private val tag = "MathTypeService"
    private val converter = StringConverter()
    // private var lastValidString    = ""
    private var beforeChangeString = ""
    // private var previouslyValid = false
    private var justEdit = false

    private val prefsName = "TypeMathPrefsFile"

    override fun onInterrupt() {
        Log.e(tag, "onInterrupt: something went wrong")
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
//        Log.i(tag, "onAccessibilityEvent: ")
//        val applicationInfo = packageManager.getApplicationInfo(event?.packageName.toString(),0)
//        val applicationLabel = packageManager.getApplicationLabel(applicationInfo)
//        Log.i(tag, "app name: $applicationLabel")
//
//        Log.i(tag, "event type: ${event?.eventType}")
        if (event?.eventType == AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED){
            try {
                val className = Class.forName(event.className.toString())
                if (EditText::class.java.isAssignableFrom(className)) {
                    // EditText is detected
                    val nodeInfo: AccessibilityNodeInfo? = event.source
                    nodeInfo?.refresh()
                    val nodeString = nodeInfo?.text.toString()
                    Log.i(tag, nodeString)
                    if(nodeString.last() == ' '){
                        // Press space

                        // get values from sharedPreferences
                        val sh : SharedPreferences = getSharedPreferences(prefsName, MODE_PRIVATE)
                        val initStr : String? = sh.getString("initString", ".")
                        val endStr : String? = sh.getString("endString", ".")

                        beforeChangeString = nodeString
                        if(initStr != null && endStr != null){
                            //do conversion only if initStr and endStr is not null

                            val str = nodeString.substring(0, nodeString.length-1)
                            if(converter.isValidFormat(str, initStr, endStr)){
                                //if string is valid

                                //Log.i(tag, "initStr: \"$initStr\" ; endStr: \"$endStr\"")
                                val converted = converter.evalString(
                                    str, initStr, endStr)
                                val bundle = Bundle()
                                bundle.putString(
                                    AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE,
                                    converted
                                )
                                nodeInfo?.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, bundle)
                                justEdit = true
                            }
                        }
                    } else {
                        justEdit = false
                    }
                }
            } catch (e: ClassNotFoundException) {
                e.printStackTrace()
            }
        }
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        val info = AccessibilityServiceInfo()
        info.apply {
            eventTypes = AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED
            feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC
            notificationTimeout = 100
        }

        this.serviceInfo = info
        Log.i(tag, "onServiceConnected")
    }
}