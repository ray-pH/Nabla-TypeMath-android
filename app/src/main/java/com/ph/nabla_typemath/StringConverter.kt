package com.ph.nabla_typemath

class StringConverter {
    private val sym = SymbolMaps()
    private val symLatex = SymbolLatexMaps()
    private val simpleMap : LinkedHashMap<String, String> = (
            (sym.symbolGreekMap
            + sym.symbolSetAndLogicMap
            + sym.symbolDomainMap
            + sym.symbolEqualityMap
            + sym.symbolCalculusMap
            + sym.symbolMiscMap) as LinkedHashMap<String, String>)
    private val fractionSlash: String = "⁄"
    private var customMap : LinkedHashMap<String, String>? = null

    // Load custom command map to customMap
    fun loadCustomMap(cMap: LinkedHashMap<String, String>?){
        this.customMap = cMap
    }

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
    private fun replaceScript(str: String, c: Char, scriptMap: LinkedHashMap<Char,Char>): String{
        if (!str.contains(c)) return str
        val keys = str.split(c)
        val unicode = keys.last().map { scriptMap[it] ?: it }
        return (keys.dropLast(1) + unicode).joinToString("")
    }

    // Replace series of character after c using map, with LaTex rule (using curly braces)
    private fun replaceScriptLatex(str: String, c: Char, scriptMap: LinkedHashMap<Char,Char>): String{
        if (!str.contains(c)) return str
        val keys = str.split(c)

        val unicodes = keys.drop(1).map{
            if(it[0] == '{'){
                val rightBraceId = it.indexOf('}')
                if(rightBraceId == -1) "^$it"
                else{
                    val toScriptStr = it.substring(1, rightBraceId)
                    val replacedStr = toScriptStr.map{ x -> scriptMap[x] ?: x }
                    it.replaceRange(0, rightBraceId+1
                        , replacedStr.joinToString(""))
                }
            }else{
                it.replaceRange(0,1, scriptMap[it[0]].toString())
            }
        }
        return (listOf(keys[0]) + unicodes).joinToString("")
    }

    // replace latex frac "\frac{<num>}{<den>} with unicode fraction
    private val latexFracRegex = "\\\\frac\\{.+\\}\\{.+\\}".toRegex()
    private fun replaceFracLatex(str: String): String{
        return str.replace(latexFracRegex){
            val itStr = it.value
            val subStr = itStr.substring(itStr.indexOf('{')+1, itStr.length-1) // ...}{..
            val (num, den) = subStr.split("}{")
            val numUnicode = num.map{ c -> sym.superscriptMap[c] ?: c}.toString()
            val denUnicode = den.map{ c -> sym.subscriptMap[c]   ?: c}.toString()
            numUnicode + fractionSlash + denUnicode
        }
    }

    // evaluate latex diacritic commands "\com{<param>}"
    private val latexDiacriticReg = "\\\\[a-zA-Z]+\\{[a-zA-Z]*\\}".toRegex()
    private fun replaceDiacriticLatex(str: String): String{
        return str.replace(latexDiacriticReg) {
            val strIt : String = it.value
            val leftId = strIt.indexOf('{')
            val keyWord = strIt.substring(1,leftId)
            val params  = strIt.substring(leftId+1, strIt.length-1)
            if(symLatex.latexDiacritic.containsKey(keyWord))
                params + symLatex.latexDiacritic[keyWord]
            else
                "\\${keyWord}{${params}}"
        }
    }

    // Replace series of character after '^' with unicode superscripts
    private fun replaceSuperscript(str: String): String{
        return replaceScript(str, '^', sym.superscriptMap)
    }

    // Replace series of character after '_' with unicode subscript
    private fun replaceSubscript(str: String): String{
        return replaceScript(str, '_', sym.subscriptMap)
    }

    // Replace series of character after '^' with unicode subscript, using LaTeX rule
    private fun replaceSuperscriptLatex(str: String): String{
        return replaceScriptLatex(str, '^', sym.superscriptMap)
    }

    // Replace series of character after '_' with unicode subscript, using LaTeX rule
    private fun replaceSubscriptLatex(str: String): String{
        return replaceScriptLatex(str, '_', sym.subscriptMap)
    }

    // Replace word that started with '\\', LaTeX style
    private val replaceStringLatexRegex = "\\\\[a-zA-Z]+".toRegex()
    private fun replaceStringLatex(str: String): String{
        return str.replace(
            replaceStringLatexRegex
        ) { (symLatex.latexMath[it.value.drop(1)] ?: it.value) }
    }

    // Replace custom commands latex
    private fun replaceCustomStringLatex(str: String): String{
        return str.replace(
            replaceStringLatexRegex
        ) { (this.customMap?.get(it.value.drop(1)) ?: it.value) }
    }

    // Return whether str is in valid format or not
    // Valid format for str is ".....<initChar>.....<endChar>(cursor)..."
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

    // evaluate fraction expression "frac <num>/<den>"
    private fun evalFraction(str: String): String {
        return sym.vulgarFractionMap[str] ?: str.let {
            val (num, den) = it.split('/')
            val numUnicode = num.map{ c -> sym.superscriptMap[c] ?: c}.toString()
            val denUnicode = den.map{ c -> sym.subscriptMap[c]   ?: c}.toString()
            numUnicode + fractionSlash + denUnicode
        }
    }

    private val fractionCommand = "frac"
    // Evaluate math expression string, convert it into unicode
    private fun evalMath(str: String,
                         useAdditionalSym : Boolean,
                         useDiacritics    : Boolean,
                         latexMode        : Boolean,
                         keepSpace        : Boolean
    ): String {
        var evaluatedString = ""
        val keys : List<String> = str.split(' ')
        if(!latexMode){
            val separator = if (keepSpace) " " else ""
            var isFrac = false
            for(key in keys) {
                if( key == fractionCommand ) { isFrac = true; continue }
                val res =
                    if(isFrac){ isFrac = false; evalFraction(key) }
                    else key
                        .let { this.customMap?.get(it) ?: it }
                        .let { replaceSuperscript(it) }
                        .let { replaceSubscript(it) }
                        .let { simpleMap[it] ?: it }
                        .let { if(!keepSpace)       addSpaceToOperator(it)            else it}
                        .let { if(useDiacritics)    symLatex.latexDiacritic[it] ?: it else it}
                        .let { if(useAdditionalSym) symLatex.latexMath[it]      ?: it else it}
                evaluatedString += (res + separator)
            }
        }else{
            for(key in keys) {
                val res = key
                    .let { replaceSuperscriptLatex(it) }
                    .let { replaceSubscriptLatex(it) }
                    .let { replaceCustomStringLatex(it) }
                    .let { replaceStringLatex(it) }
                    .let { if(useDiacritics) replaceDiacriticLatex(it) else it }
                    .let { replaceFracLatex(it) }
                evaluatedString += "$res "
            }
            evaluatedString.dropLast(1)
        }
        return evaluatedString
    }

    fun evalString(str: String, initStr: String, endStr: String,
                   useAdditionalSym : Boolean,
                   useDiacritics    : Boolean,
                   latexMode        : Boolean,
                   keepSpace        : Boolean
    ): String {
        val n = 1 + endStr.count{ initStr.contains(it) }
        val id = indexOfFromBack(initStr, str, n)
        return if (id < 0) str
        else {
            val headString  = str.substring(0, id)
            val validString = str.substring(id + initStr.length,
                                            str.length - endStr.length)
            headString + evalMath(validString, useAdditionalSym,
                                  useDiacritics, latexMode, keepSpace)
        }
    }
}