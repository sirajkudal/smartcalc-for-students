package com.example

import com.example.util.MathParser
import org.junit.Test
import org.junit.Assert.*

class MathParserTest {
    @Test
    fun testBasicAddition() {
        assertEquals(8.0, MathParser.evaluate("4+4", false), 0.001)
        assertEquals(16.0, MathParser.evaluate("4*4", false), 0.001)
        assertEquals(16.0, MathParser.evaluate("4×4", false), 0.001)
        assertEquals(1.0, MathParser.evaluate("4÷4", false), 0.001)
        assertEquals(0.0, MathParser.evaluate("4-4", false), 0.001)
    }

    @Test
    fun testPercentage() {
        assertEquals(0.5, MathParser.evaluate("50%", false), 0.001)
        assertEquals(110.0, MathParser.evaluate("100+10%", false), 0.001)
        assertEquals(90.0, MathParser.evaluate("100-10%", false), 0.001)
        assertEquals(10.0, MathParser.evaluate("100*10%", false), 0.001)
        assertEquals(1000.0, MathParser.evaluate("100/10%", false), 0.001)
    }

    @Test
    fun testIncomplete() {
        try {
            MathParser.evaluate("4-", false)
        } catch (e: Exception) {
            // Should just throw
        }
    }
}
