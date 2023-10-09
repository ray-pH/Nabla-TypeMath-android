package com.ph.nabla_typemath

import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class UnitTest {

    @Test
    fun common_examples(){
        val sc = StringConverter()
        val param_latex = MathTypeService.Parameters("test")
        param_latex.latexMode = true
        param_latex.quickMode = false
        val param_quick = MathTypeService.Parameters("test")
        param_quick.latexMode = false
        param_quick.quickMode = true

        assertEquals("∫ f(x) dx ∈ ℝ ",
            sc.evalString(".\\int f(x) dx \\in \\mathbb{R}.", param_latex))
        assertEquals("α²ⁿ⁺¹ = β ",
            sc.evalString(".\\alpha^{2n+1} = \\beta.", param_latex))
        assertEquals("∇² ",
            sc.evalString(".\\nabla^2.", param_latex))

        assertEquals("∫",
            sc.evalString(".int", param_quick))
        assertEquals("x²ⁿ⁺¹",
            sc.evalString(".x^2n+1", param_quick))
    }
    @Test
    fun frac_is_correct() {
        val sc = StringConverter()

        val param_latex = MathTypeService.Parameters("test")
        param_latex.latexMode = true
        param_latex.quickMode = false
        assertEquals("½ ", sc.evalString(".\\frac{1}{2}.", param_latex))
        assertEquals("³⁄₇ ", sc.evalString(".\\frac{3}{7}.", param_latex))

        val param_quick = MathTypeService.Parameters("test")
        param_quick.latexMode = false
        param_quick.quickMode = true

        assertEquals("½", sc.evalString(".1/2", param_quick))
        assertEquals("³⁄₇", sc.evalString(".3/7", param_quick))
    }
}