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
    fun isValidFormat(str: String, initStr: String, endStr: String, quickMode: Boolean): Boolean {
        if(!quickMode) {
            // check if string is long enough
            if(str.length <= endStr.length) return false
            if(str.length <= endStr.length + initStr.length) return false

            // check if last part of string is endStr
            for(i in endStr.indices){
                val j = str.length - endStr.length + i
                if(str[j] != endStr[i]) return false
            }
        }else{
            if(str.length <= initStr.length) return false
        }

        // check if initStr exist
        val headStr = if (quickMode) str
                    else str.substring(0, str.length - endStr.length)
        val initStrId = headStr.lastIndexOf(initStr)
        if(initStrId == -1) return false

        // check if first char after initStr is not space
        if (headStr[initStrId + initStr.length] == ' ') return false

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

    // Evaluate only a single command key
    private fun evalSingle(key: String, param: MathTypeService.Parameters): String{
        return key
            .let { this.customMap?.get(it) ?: it }
            .let { replaceSuperscript(it)        }
            .let { replaceSubscript(it)          }
            .let { simpleMap[it] ?: it           }
            .let { if(!param.keepSpace)       addSpaceToOperator(it)           else it}
            .let { if(param.useDiacritics)    symLatex.latexDiacritic[it]?: it else it}
            .let { if(param.useAdditionalSym) symLatex.latexMath[it]     ?: it else it}
    }

    private val fractionCommand = "frac"
    // Evaluate math expression string, convert it into unicode
    private fun evalMath(str: String, param: MathTypeService.Parameters): String {
        var evaluatedString = ""
        val keys : List<String> = str.split(' ')
        if(!param.latexMode){
            val separator = if (param.keepSpace) " " else ""
            var isFrac = false
            for(key in keys) {
                if( key == fractionCommand ) { isFrac = true; continue }
                val res =
                    if(isFrac){ isFrac = false; evalFraction(key) }
                    else evalSingle(key, param)
                val sepCheck = if (res == key) " " else separator
                evaluatedString += (res + sepCheck)
            }
        }else{
            for(key in keys) {
                val res = key
                    .let { replaceSuperscriptLatex(it) }
                    .let { replaceSubscriptLatex(it) }
                    .let { replaceCustomStringLatex(it) }
                    .let { replaceStringLatex(it) }
                    .let { if(param.useDiacritics) replaceDiacriticLatex(it) else it }
                    .let { replaceFracLatex(it) }
                evaluatedString += "$res "
            }
        }
        if (evaluatedString.last() == ' ') evaluatedString.dropLast(1)
        return evaluatedString
    }

    fun evalString(str: String, param: MathTypeService.Parameters): String {
        val endStr  = param.endStr
        val initStr = param.initStr
        if(endStr.isNullOrBlank() || initStr.isNullOrBlank()) return str

        val endLen = if(param.quickMode) 0 else endStr.length
        val id = str
            .substring(0, str.length - endLen)
            .lastIndexOf(initStr)
        if (id < 0) return str
        val headString  = str.substring(0, id)
        val validString = str.substring(id + initStr.length, str.length - endLen)
        val evaluated   =
            if (param.quickMode) evalSingle(validString, param)
            else evalMath(validString, param)
        return if (evaluated == validString) str
        else headString + evaluated
    }
}
