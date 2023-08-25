package com.ph.nabla_typemath

import android.accessibilityservice.AccessibilityService
import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.os.Bundle
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.widget.EditText


class MathTypeService : AccessibilityService() {
//    private val tag = "NablaMathTypeService"

    private val converter   = StringConverter()
    private val gsonHandler = CustomCommandGSONHandler()

    data class Parameters(val name: String) {
        var initStr          : String? = "."
        var endStr           : String? = "."
        var quickMode        : Boolean = true
        var latexMode        : Boolean = false
        var useAdditionalSym : Boolean = false
        var keepSpace        : Boolean = false
        var useDiacritics    : Boolean = true
    }
    private val param : Parameters = Parameters("main")

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
//        Log.e(tag, "onInterrupt: something went wrong")
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event?.eventType == AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED){ try {
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
                    val curInitStr = param.initStr
                    val curEndStr  = param.endStr
                    if(curInitStr != null && curEndStr != null){
                        //do conversion only if initStr and endStr is not null
                        val toConvertStr = headStr.substring(0, headStr.length-1)
                        if(converter.isValidFormat(toConvertStr,
                                curInitStr, curEndStr, param.quickMode)
                        ){
                            //if string is valid
                            val converted = converter.evalString(toConvertStr, param)
                            if(converted != toConvertStr){
                                val newCursorPos = cursorPos - 1 +
                                        (converted.length - toConvertStr.length)

                                try {
                                    textViewNodePutString(nodeInfo, (converted+tailStr))
                                    textViewNodePutCursor(nodeInfo, newCursorPos)
                                }
                                catch (e : Exception) { e.printStackTrace() }

                                afterChangeStringLen  = converted.length + tailStr.length
                                afterChangeCursorPos  = newCursorPos
                                beforeChangeCursorPos = cursorPos
                                beforeChangeString    = nodeString
                                justEdit = true
                            }
                        }
                    }
                } else {
                    justEdit = false
                }
                prevStringLen = nodeString.length
            }
        } catch (e: Exception) { e.printStackTrace() } }
    }

    private fun updatePrefsParameters(prefs: SharedPreferences){
        param.initStr          = prefs.getString("initString", ".")
        param.endStr           = prefs.getString("endString", ".")
        param.quickMode        = prefs.getBoolean("quickMode", false)
        param.latexMode        = prefs.getBoolean("latexMode", false)
        param.useAdditionalSym = prefs.getBoolean("useAdditionalSymbols", false)
        param.keepSpace        = prefs.getBoolean("keepSpace", false)
        param.useDiacritics    = prefs.getBoolean("useDiacritics", true)
    }

    private fun updateConverterCustomMap(prefs: SharedPreferences) {
        val gSONStr = prefs.getString("customMap", "") ?: ""
        if (gSONStr.isNotEmpty()) {
            val customMap: LinkedHashMap<String, String> = gsonHandler.gSONStrToLinkedMap(gSONStr)
            converter.loadCustomMap(customMap)
        }
    }

    private fun onPreferenceChanges(prefs: SharedPreferences, key: String?){
        try{
            if(key == "customMap"){
                updateConverterCustomMap(prefs)
            }else{
                // if key == null (prefs is cleared), also go here
                updatePrefsParameters(prefs)
            }
        }
        catch(e : Exception){ e.printStackTrace() }
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
//        this.serviceInfo = info
//        Log.i(tag, "onServiceConnected")

        try{
            val sh : SharedPreferences = getSharedPreferences(prefsName, MODE_PRIVATE)
            sh.registerOnSharedPreferenceChangeListener(
                OnSharedPreferenceChangeListener { prefs, key -> onPreferenceChanges(prefs,key)
                }.also { this.listener = it }
            )
            updateConverterCustomMap(sh)
            updatePrefsParameters(sh)
        }
        catch(e: Exception){
            e.printStackTrace()
//            Log.e(tag, "Something went wrong  when trying to register sharedPreferences listener")
        }
    }
}