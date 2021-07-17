package com.ph.typemath

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.widget.EditText


class MathTypeService : AccessibilityService() {
    private val nodeInfo: AccessibilityNodeInfo? = null
    private val tag = "MathTypeService"

    override fun onInterrupt() {
        Log.e(tag, "onInterrupt: something went wrong")
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        Log.i(tag, "onAccessibilityEvent: ")
        val applicationInfo = packageManager.getApplicationInfo(event?.packageName.toString(),0)
        val applicationLabel = packageManager.getApplicationLabel(applicationInfo)
        Log.i(tag, "app name: $applicationLabel")

        Log.i(tag, "event type: ${event?.eventType}")
        if (event?.eventType == AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED){
            try {
                val className = Class.forName(event.className.toString())
                if (EditText::class.java.isAssignableFrom(className)) {
                    val nodeInfo: AccessibilityNodeInfo? = event.source
                    nodeInfo?.refresh()
                    val nodeText = nodeInfo?.text
                    Log.i(tag, nodeText.toString())
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
//            eventTypes = AccessibilityEvent.TYPE_VIEW_CLICKED or AccessibilityEvent.TYPE_VIEW_FOCUSED or AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED
            feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC
            notificationTimeout = 100
        }

        this.serviceInfo = info
        Log.i(tag, "onServiceConnected: ")
    }
}