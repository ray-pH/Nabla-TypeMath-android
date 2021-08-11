package com.ph.typemath

import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.transition.AutoTransition
import androidx.transition.TransitionManager

class SymbolsActivity : AppCompatActivity() {
//    private var cardView: CardView? = null

//    private var greekSymbolButton: ImageButton? = null
//    private var greekSymbolView: LinearLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_symbols)

        val cardView          : CardView?     = findViewById(R.id.base_cardView)
        val greekSymbolButton : ImageButton?  = findViewById(R.id.greekSymbol_button)
        val greekSymbolView   : LinearLayout? = findViewById(R.id.greekSymbol_view)

        greekSymbolButton?.setOnClickListener {
            if(cardView != null){
                TransitionManager.beginDelayedTransition(cardView, AutoTransition())
                val isVisible = greekSymbolView?.visibility == View.VISIBLE
                greekSymbolView?.visibility = if (isVisible) View.GONE else View.VISIBLE
                greekSymbolButton.setImageResource(
                    if (isVisible) R.drawable.ic_baseline_expand_more_24
                    else           R.drawable.ic_baseline_expand_less_24
                )
            }
        }
    }
}