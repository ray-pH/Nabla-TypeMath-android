package com.ph.nabla_typemath

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.transition.AutoTransition
import androidx.transition.TransitionManager

class SymbolsActivity : AppCompatActivity() {
    private val sym = SymbolMaps()
    private enum class SymbolsName {
        GREEK,
        SET_AND_LOGIC,
        DOMAIN,
        EQUALITY,
        CALCULUS,
        MISC,
    }
    private val createdBoolean : BooleanArray = BooleanArray(SymbolsName.values().size) { false }

    private val tableRowLayoutParam : TableRow.LayoutParams = TableRow.LayoutParams(
        TableRow.LayoutParams.MATCH_PARENT,
        TableRow.LayoutParams.WRAP_CONTENT
    )
    private fun tableLayoutAddNewRow(table: TableLayout?, str1: String, str2: String){
        val textView1  = TextView(this)
        val textView2  = TextView(this)
        textView1.text = str1
        textView2.text = str2
        textView1.layoutParams = tableRowLayoutParam
        textView2.layoutParams = tableRowLayoutParam

        val row = TableRow(this)
        row.layoutParams = tableRowLayoutParam
        row.addView(textView1)
        row.addView(textView2)
        table?.addView(row,
            TableLayout.LayoutParams(
                TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT
            )
        )
    }
    private fun setImageButtonClickExpander(
        button: ImageButton?, cardView: CardView?, view: LinearLayout?, symTable: TableLayout?,
        symName: SymbolsName, symMap: LinkedHashMap<String,String>
    ){
        button?.setOnClickListener {
            if(cardView != null){
                if(!createdBoolean[symName.ordinal]){
                    symMap.forEach { (key, value) -> tableLayoutAddNewRow(symTable, key, value) }
                    createdBoolean[symName.ordinal] = true
                }
                TransitionManager.beginDelayedTransition(cardView, AutoTransition())
                val isVisible = view?.visibility == View.VISIBLE
                view?.visibility = if (isVisible) View.GONE else View.VISIBLE
                button.setImageResource(
                    if (isVisible) R.drawable.ic_baseline_expand_more_24
                    else           R.drawable.ic_baseline_expand_less_24
                )
            }
        }
    }


    private fun setExpanderByName(name: String, symName: SymbolsName, symMap: LinkedHashMap<String, String>){
        val cardView: CardView? = findViewById(
            this.resources.getIdentifier("${name}_cardView", "id", this.packageName)
        )
        val button: ImageButton? = findViewById(
            this.resources.getIdentifier("${name}_button", "id", this.packageName)
        )
        val view: LinearLayout? = findViewById(
            this.resources.getIdentifier("${name}_view", "id", this.packageName)
        )
        val tableLayout : TableLayout? = findViewById(
            this.resources.getIdentifier("${name}_table", "id", this.packageName)
        )
        setImageButtonClickExpander(button, cardView, view, tableLayout, symName, symMap)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_symbols)

        setExpanderByName("greekSymbol", SymbolsName.GREEK, sym.symbolGreekMap)
        setExpanderByName("setAndLogic", SymbolsName.SET_AND_LOGIC, sym.symbolSetAndLogicMap)
        setExpanderByName("domains", SymbolsName.DOMAIN, sym.symbolDomainMap)
        setExpanderByName("equality", SymbolsName.EQUALITY, sym.symbolEqualityMap)
        setExpanderByName("calculus", SymbolsName.CALCULUS, sym.symbolCalculusMap)
        setExpanderByName("misc", SymbolsName.MISC, sym.symbolMiscMap)
    }
}