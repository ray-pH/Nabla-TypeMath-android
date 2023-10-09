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
    fun frac_is_correct() {
        val param = MathTypeService.Parameters("test")
        param.latexMode = true
        param.quickMode = false
        val x = StringConverter()
        assertEquals("½ ", x.evalString(".\\frac{1}{2}.", param))
        assertEquals("³⁄₇ ", x.evalString(".\\frac{3}{7}.", param))
    }
}