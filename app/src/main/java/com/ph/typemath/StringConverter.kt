package com.ph.typemath

class StringConverter {
    // Return the index of n-th c in str
    // If c does not exist in str, return -1
    private fun indexOfFromBack(c: Char, str: String, n: Int): Int {
        var count = 0
        for (i in str.length-1 downTo 0) if (str[i] == c) if (++count >= n) return i
        return -1
    }

    // Replace series of character after '^' with unicode superscripts
    private fun replaceSuperscript(str: String): String{
        val superscriptsMap : HashMap<Char, Char> = hashMapOf('m' to 'ᵐ', 'n' to 'ⁿ')

        if (!str.contains('^')) return str
        val keys = str.split('^')
        val unicodeSuper = keys.last().map {
            if (superscriptsMap.containsKey(it)) superscriptsMap[it] else it
        }
        return (keys.dropLast(1) + unicodeSuper).joinToString("")
    }

    // Return whether str is in valid format or not
    // Valid format for str is ".....<initChar>.....<endChar>"
    fun isValidFormat(str: String, initChar: Char, endChar: Char): Boolean {
        // check if last char typed is endChar
        if (str.last() != endChar) return false
        // check if initChar exist
        val n = if (initChar == endChar) 2 else 1
        if (indexOfFromBack(initChar, str, n) == -1) return false
        return true
    }

    // Get the valid string in between initChar and endChar
//    fun getValidString(str: String, initChar: Char, endChar: Char): String {
//        val n = if (initChar == endChar) 2 else 1
//        val id = indexOfFromBack(initChar, str, n)
//        return if (id < 0) ""
//        else str.substring(id+1,str.length-1)
//    }

    private fun addSpaceToOperator(str: String): String {
        return when (str) {
            "+" -> " + "
            "-" -> " - "
            "=" -> " = "
            else -> str
        }
    }

    // Evaluate math expression string, convert it into unicode
    private fun evalMath(str: String): String {
        val keys : List<String> = str.split(' ')
            .map{ replaceSuperscript(it) }
            .map{ addSpaceToOperator(it) }
        return keys.joinToString("")
    }

    fun evalString(str: String, initChar: Char, endChar: Char): String {
        val n = if (initChar == endChar) 2 else 1
        val id = indexOfFromBack(initChar, str, n)
        return if (id < 0) str
        else {
            val validString = str.substring(id+1,str.length-1)
            val headString  = str.substring(0,id)
            headString + evalMath(validString)
        }
    }
}