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
    private var afterChangeStringLen = -1
    private var beforeChangeString = ""
    private var prevStringLen = -99
    //private var afterChangeString  = ""
    //private var afterChangeCursor  = -1
    private var justEdit = false

    private val prefsName = "TypeMathPrefsFile"

    private fun textViewNodePutString(node: AccessibilityNodeInfo?, str: String){
        val strBundle = Bundle()
        strBundle.putString(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, str)
        node?.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, strBundle)
    }

    private fun textViewNodePutCursor(node: AccessibilityNodeInfo?, pos: Int){
        val cursorBundle = Bundle()
        cursorBundle.putInt(AccessibilityNodeInfo.ACTION_ARGUMENT_SELECTION_START_INT, pos)
        cursorBundle.putInt(AccessibilityNodeInfo.ACTION_ARGUMENT_SELECTION_END_INT, pos)
        node?.performAction(AccessibilityNodeInfo.ACTION_SET_SELECTION, cursorBundle)
    }

    override fun onInterrupt() {
        Log.e(tag, "onInterrupt: something went wrong")
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        // TODO : only undo if user is deleting the last char of edited string
        if (event?.eventType == AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED){
            try {
                val className = Class.forName(event.className.toString())
                if (EditText::class.java.isAssignableFrom(className)) {
                    // EditText is detected
                    val nodeInfo: AccessibilityNodeInfo? = event.source
                    nodeInfo?.refresh()
                    val nodeString = nodeInfo?.text.toString()

                    // Split string by cursor
                    val tryCursorPos = nodeInfo?.textSelectionEnd
                    val cursorPos    = (
                            if (tryCursorPos != -1) tryCursorPos else nodeString.length) ?: 0
                    val headStr      = nodeString.substring(0, cursorPos)
                    val tailStr      = nodeString.substring(cursorPos)
                    // Log.i(tag, "headStr: \"$headStr\" ; tailStr: \"$tailStr\"")

                    if(justEdit && nodeString.length == afterChangeStringLen - 1){
                        // User delete last char right after edit
                        textViewNodePutString(nodeInfo, beforeChangeString)
                        justEdit = false
                    }
                    else if(
                        headStr.isNotEmpty()  &&
                        headStr.last() == ' ' &&
                        nodeString.length == prevStringLen + 1
                    ){
                        // Press space

                        // get values from sharedPreferences
                        val sh : SharedPreferences = getSharedPreferences(prefsName, MODE_PRIVATE)
                        val initStr          : String? = sh.getString("initString", ".")
                        val endStr           : String? = sh.getString("endString", ".")
                        val latexMode        : Boolean = sh.getBoolean("latexMode", false)
                        val useAdditionalSym : Boolean = sh.getBoolean("useAdditionalSymbols", false)
                        val keepSpace        : Boolean = sh.getBoolean("keepSpace", false)
                        val useDiacritics = false

                        if(initStr != null && endStr != null){
                            //do conversion only if initStr and endStr is not null
                            val toConvertStr = headStr.substring(0, headStr.length-1)
                            if(converter.isValidFormat(toConvertStr, initStr, endStr)){
                                //if string is valid
                                val converted = converter.evalString(
                                    toConvertStr, initStr, endStr,
                                    useAdditionalSym, useDiacritics, latexMode, keepSpace
                                )
                                val newCursorPos = cursorPos - 1 +
                                        (converted.length - toConvertStr.length)

                                textViewNodePutString(nodeInfo, (converted+tailStr))
                                textViewNodePutCursor(nodeInfo, newCursorPos)

                                afterChangeStringLen = converted.length + tailStr.length
                                beforeChangeString = nodeString
                                justEdit = true
                            }
                        }
                    } else {
                        justEdit = false
                    }
                    prevStringLen = nodeString.length
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