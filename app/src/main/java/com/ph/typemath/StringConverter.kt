package com.ph.typemath

class StringConverter {
    private val sym = SymbolMaps()

    // Return the index of n-th checkStr in str
    // If n-th checkStr does not exist in str, return -1
    private fun indexOfFromBack(checkStr: String, str: String, n: Int): Int{
        var count = 0
        for(i in str.length-1 downTo 0){
            if(str[i] == checkStr[0]){
                var found = true
                for(j in checkStr.indices){
                    if(str[i+j] != checkStr[j]){
                        found = false
                        break
                    }
                }
                if(found) count++
                if(count == n) return i
            }
        }
        return -1
    }

    // Replace series of character after '^' with unicode superscripts
    private fun replaceSuperscript(str: String): String{
        if (!str.contains('^')) return str
        val keys = str.split('^')
        val unicodeSuper = keys.last().map {
            if (sym.superscriptMap.containsKey(it)) sym.superscriptMap[it] else it
        }
        return (keys.dropLast(1) + unicodeSuper).joinToString("")
    }

    // Replace series of character after '_' with unicode subscript
    private fun replaceSubscript(str: String): String{
        if (!str.contains('_')) return str
        val keys = str.split('_')
        val unicodeSuper = keys.last().map {
            if (sym.subscriptMap.containsKey(it)) sym.subscriptMap[it] else it
        }
        return (keys.dropLast(1) + unicodeSuper).joinToString("")
    }

    // Return whether str is in valid format or not
    // Valid format for str is ".....<initChar>.....<endChar>"
    fun isValidFormat(str: String, initStr: String, endStr: String): Boolean {
        // check if last part of string is endStr
        for(i in endStr.indices){
            val j = str.length - endStr.length + i
            if(str[j] != endStr[i]) return false
        }

        // check if initStr exist
        val n = 1 + endStr.count{ initStr.contains(it) }
        if(indexOfFromBack(initStr, str, n) == -1) return false
        return true
    }

    private fun addSpaceToOperator(str: String): String {
        return when (str) {
            "+" -> " + "
            "-" -> " - "
            "=" -> " = "
            ""  -> " "
            else -> str
        }
    }

    // Evaluate math expression string, convert it into unicode
    private fun evalMath(str: String): String {
        val keys : List<String> = str.split(' ')
            .map{ replaceSuperscript(it) }
            .map{ replaceSubscript(it)   }
            .map{ addSpaceToOperator(it) }
        return keys.joinToString("")
    }

    fun evalString(str: String, initStr: String, endStr: String): String {
        val n = 1 + endStr.count{ initStr.contains(it) }
        val id = indexOfFromBack(initStr, str, n)
        return if (id < 0) str
        else {
            val validString = str.substring(id + initStr.length,
                                            str.length - endStr.length)
            val headString  = str.substring(0,id)
            headString + evalMath(validString)
        }
    }
}