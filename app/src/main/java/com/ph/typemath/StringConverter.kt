package com.ph.typemath

class StringConverter {
    private val sym = SymbolMaps()
    private val symLatex = SymbolLatexMaps()
    private val simpleMap : HashMap<String, String> = (
            (sym.symbolGreekMap
            + sym.symbolSetAndLogicMap
            + sym.symbolDomainMap
            + sym.symbolEqualityMap
            + sym.symbolCalculusMap
            + sym.symbolMiscMap) as HashMap<String, String>)


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

    // Replace series of character after c using map
    private fun replaceScript(str: String, c: Char, map: HashMap<Char,Char>): String{
        if (!str.contains(c)) return str
        val keys = str.split(c)
        val unicode = keys.last().map {
            if (map.containsKey(it)) map[it] else it
        }
        return (keys.dropLast(1) + unicode).joinToString("")
    }

    // Replace series of character after '^' with unicode superscripts
    private fun replaceSuperscript(str: String): String{
        return replaceScript(str, '^', sym.superscriptMap)
    }

    // Replace series of character after '_' with unicode subscript
    private fun replaceSubscript(str: String): String{
        return replaceScript(str, '_', sym.subscriptMap)
    }

    //Replace Symbols
    private fun replaceSymbols(str: String, map: HashMap<String, String>) : String{
        return if (map.containsKey(str)) (map[str] ?: str) else str
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
    private fun evalMath(str: String,
                         useAdditionalSym : Boolean,
                         useDiacritics    : Boolean,
                         useLatexOnly     : Boolean,
                         keepSpace        : Boolean
    ): String {
        // val useAdditionalSym = false
        // val useDiacritics    = true
        // val useLatexOnly     = false

        var keys : List<String> = if(!useLatexOnly) {
            str.split(' ')
                .asSequence()
                .map { replaceSuperscript(it) }
                .map { replaceSubscript(it) }
                .map { replaceSymbols(it, simpleMap)}
                .toList()
        }else{
            str.split(' ')
            //TODO : ADD Latex Mode
        }

        if(!keepSpace)       keys = keys.map{ addSpaceToOperator(it) }
        if(useDiacritics)    keys = keys.map{ replaceSymbols(it, symLatex.latexDiacriticMath)}
        if(useAdditionalSym) keys = keys.map{ replaceSymbols(it, symLatex.latexMath)}

        return keys.joinToString((if (keepSpace) " " else ""))
    }

    fun evalString(str: String, initStr: String, endStr: String,
                   useAdditionalSym : Boolean,
                   useDiacritics    : Boolean,
                   useLatexOnly     : Boolean,
                   keepSpace        : Boolean
    ): String {
        val n = 1 + endStr.count{ initStr.contains(it) }
        val id = indexOfFromBack(initStr, str, n)
        return if (id < 0) str
        else {
            val validString = str.substring(id + initStr.length,
                                            str.length - endStr.length)
            val headString  = str.substring(0,id)
            headString + evalMath(validString, useAdditionalSym,
                                  useDiacritics, useLatexOnly, keepSpace)
        }
    }
}