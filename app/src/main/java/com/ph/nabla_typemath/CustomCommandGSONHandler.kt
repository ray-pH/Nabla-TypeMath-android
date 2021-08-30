package com.ph.nabla_typemath

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type


class CustomCommandGSONHandler {
    private val gson = Gson()

    fun linkedMapToGSONStr(linkedMap: LinkedHashMap<String, String>): String {
//        return gson.toJson(linkedMap, LinkedHashMap::class.java)
        return gson.toJson(linkedMap)
    }

    fun gSONStrToLinkedMap(GSONStr: String): LinkedHashMap<String, String> {
        val type: Type = object : TypeToken<LinkedHashMap<String?, String?>?>() {}.type
        return gson.fromJson(GSONStr, type)
    }

    fun gSONValidMap(GSONStr: String): Boolean {
        return try {
            val type: Type = object : TypeToken<LinkedHashMap<String?, String?>?>() {}.type
            val map : LinkedHashMap<String,String>? = gson.fromJson(GSONStr, type)
            map != null
        } catch(e : Exception) {
            false
        }
    }
}