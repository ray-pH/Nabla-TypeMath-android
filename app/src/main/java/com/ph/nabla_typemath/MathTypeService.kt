package com.ph.nabla_typemath

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.os.Bundle
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.widget.EditText


class MathTypeService : AccessibilityService() {
    private val tag = "NablaMathTypeService"

    private val converter   = StringConverter()
    private val gsonHandler = CustomCommandGSONHandler()

    private var initStr          : String? = "."
    private var endStr           : String? = "."
    private var latexMode        : Boolean = false
    private var useAdditionalSym : Boolean = false
    private var keepSpace        : Boolean = false
    private var useDiacritics    : Boolean = true

    private var prevStringLen = -99
    private var beforeChangeString    = ""
    private var afterChangeStringLen  = -1
    private var beforeChangeCursorPos = -1
    private var afterChangeCursorPos  = -1
    private var justEdit = false

    private val prefsName = "TypeMathPrefsFile"
    private var listener: OnSharedPreferenceChangeListener? = null

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

    // TODO : Use TYPE_VIEW_TEXT_CHANGED properties rather than private variables from
    //  https://developer.android.com/reference/android/view/accessibility/AccessibilityEvent
    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
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

                    if(
                        justEdit          &&
                        cursorPos         == afterChangeCursorPos - 1 &&
                        nodeString.length == afterChangeStringLen - 1
                    ){
                        // User delete last char right after edit
                        textViewNodePutString(nodeInfo, beforeChangeString)
                        textViewNodePutCursor(nodeInfo, beforeChangeCursorPos)
                        justEdit = false
                    }
                    else if(
                        headStr.isNotEmpty()  &&
                        headStr.last() == ' ' &&
                        nodeString.length == prevStringLen + 1
                    ){
                        // Press space

                        // get values from sharedPreferences
                        val curInitStr = initStr
                        val curEndStr  = endStr
                        if(curInitStr != null && curEndStr != null){
                            //do conversion only if initStr and endStr is not null
                            val toConvertStr = headStr.substring(0, headStr.length-1)
                            if(converter.isValidFormat(toConvertStr, curInitStr, curEndStr)){
                                //if string is valid
                                val converted = converter.evalString(
                                    toConvertStr, curInitStr, curEndStr,
                                    useAdditionalSym, useDiacritics, latexMode, keepSpace
                                )
                                val newCursorPos = cursorPos - 1 +
                                        (converted.length - toConvertStr.length)

                                textViewNodePutString(nodeInfo, (converted+tailStr))
                                textViewNodePutCursor(nodeInfo, newCursorPos)

                                afterChangeStringLen  = converted.length + tailStr.length
                                afterChangeCursorPos  = newCursorPos
                                beforeChangeCursorPos = cursorPos
                                beforeChangeString    = nodeString
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

    private fun updatePrefsParameters(prefs: SharedPreferences){
        initStr          = prefs.getString("initString", ".")
        endStr           = prefs.getString("endString", ".")
        latexMode        = prefs.getBoolean("latexMode", false)
        useAdditionalSym = prefs.getBoolean("useAdditionalSymbols", false)
        keepSpace        = prefs.getBoolean("keepSpace", false)
        useDiacritics    = prefs.getBoolean("useDiacritics", true)
    }

    private fun updateConverterCustomMap(prefs: SharedPreferences) {
        val gSONStr = prefs.getString("customMap", "") ?: ""
        if (gSONStr.isNotEmpty()) {
            val customMap: LinkedHashMap<String, String> = gsonHandler.gSONStrToLinkedMap(gSONStr)
            converter.loadCustomMap(customMap)
        }

    }

    private fun onPreferenceChanges(prefs: SharedPreferences, key: String){
        if(key == "customMap"){
            updateConverterCustomMap(prefs)
        }else{
            updatePrefsParameters(prefs)
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

        try{
            val sh : SharedPreferences = getSharedPreferences(prefsName, MODE_PRIVATE)
            sh.registerOnSharedPreferenceChangeListener(
                OnSharedPreferenceChangeListener { prefs, key -> onPreferenceChanges(prefs,key)
                }.also { this.listener = it }
            )
            updateConverterCustomMap(sh)
            updatePrefsParameters(sh)
        }catch(e: Exception){
            Log.e(tag, "Something went wrong  when trying to register sharedPreferences listener")
        }
    }
}