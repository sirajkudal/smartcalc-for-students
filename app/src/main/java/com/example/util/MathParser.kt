package com.example.util

import kotlin.math.*

object MathParser {
    /**
     * Evaluates a mathematical expression string.
     * @param expression The mathematical expression (e.g. "sin(30) + logbase(2,8)")
     * @param isDegree Whether trigonometric functions should treat values as degrees.
     */
    fun evaluate(expression: String, isDegree: Boolean): Double {
        val sanitized = expression
            .replace("×", "*")
            .replace("÷", "/")
            .replace("π", PI.toString())
            .replace("e", E.toString())
            // Replace absolute value bars like |x| with abs(x)
            // To do this simply, we parse the direct string. Our parser can handle | expressions!
            .replace(" ", "")
        
        return Parser(sanitized, isDegree).parse()
    }

    private class Parser(val input: String, val isDegree: Boolean) {
        var pos = -1
        var ch = ' '

        fun nextChar() {
            pos++
            ch = if (pos < input.length) input[pos] else '\u0000'
        }

        fun eat(charToEat: Char): Boolean {
            while (ch == ' ') nextChar()
            if (ch == charToEat) {
                nextChar()
                return true
            }
            return false
        }

        fun parse(): Double {
            nextChar()
            val x = parseExpression()
            if (pos < input.length) throw RuntimeException("Unexpected character: $ch")
            return x
        }

        // expression = term | expression '+' term | expression '-' term
        fun parseExpression(): Double {
            var x = parseTerm(null)
            while (true) {
                if (eat('+')) x += parseTerm(x) // addition with potential % target
                else if (eat('-')) x -= parseTerm(x) // subtraction with potential % target
                else break
            }
            return x
        }

        // term = factor | term '*' factor | term '/' factor
        fun parseTerm(percentTarget: Double? = null): Double {
            var x = parseFactor(percentTarget)
            while (true) {
                if (eat('*')) x *= parseFactor(null) // multiplication
                else if (eat('/')) {
                    val divisor = parseFactor(null)
                    if (divisor == 0.0) throw ArithmeticException("Division by zero")
                    x /= divisor // division
                }
                else break
            }
            return x
        }

        // factor = '+' factor | '-' factor | '(' expression ')' | number | functionName factor | factor '^' factor
        fun parseFactor(percentTarget: Double? = null): Double {
            if (eat('+')) return parseFactor(percentTarget) // unary plus
            if (eat('-')) return -parseFactor(percentTarget) // unary minus

            var x: Double
            val startPos = this.pos
            if (eat('(')) { // parentheses
                x = parseExpression()
                eat(')')
            } else if (ch in '0'..'9' || ch == '.') { // numbers
                while (ch in '0'..'9' || ch == '.') nextChar()
                val str = input.substring(startPos, pos)
                x = str.toDoubleOrNull() ?: throw RuntimeException("Invalid number: $str")
            } else if (ch == '|') { // absolute value: |expression|
                nextChar() // eat the first |
                x = parseExpression()
                eat('|') // eat the second |
                x = abs(x)
            } else if (ch in 'a'..'z' || ch == '⁻' || ch == '¹' || ch == '√') { // functions & constants
                if (ch == '√') {
                    nextChar()
                    x = parseFactor(null)
                    x = sqrt(x)
                } else {
                    while (ch in 'a'..'z' || ch in '0'..'9' || ch == '⁻' || ch == '¹') nextChar()
                    val func = input.substring(startPos, pos)
                    
                    if (func == "logbase" || func == "logN") {
                        if (ch == '(') {
                            eat('(')
                            val base = parseExpression()
                            eat(',')
                            val value = parseExpression()
                            eat(')')
                            x = ln(value) / ln(base)
                        } else {
                            throw RuntimeException("logbase requires (base,value)")
                        }
                    } else {
                        val arg = if (ch == '(') {
                            eat('(')
                            val res = parseExpression()
                            eat(')')
                            res
                        } else {
                            parseFactor(null)
                        }
                        
                        x = when (func) {
                            "sqrt" -> sqrt(arg)
                            "sin" -> {
                                val rad = if (isDegree) Math.toRadians(arg) else arg
                                sin(rad)
                            }
                            "cos" -> {
                                val rad = if (isDegree) Math.toRadians(arg) else arg
                                cos(rad)
                            }
                            "tan" -> {
                                val rad = if (isDegree) Math.toRadians(arg) else arg
                                tan(rad)
                            }
                            "asin", "sin⁻¹" -> {
                                val res = asin(arg)
                                if (isDegree) Math.toDegrees(res) else res
                            }
                            "acos", "cos⁻¹" -> {
                                val res = acos(arg)
                                if (isDegree) Math.toDegrees(res) else res
                            }
                            "atan", "tan⁻¹" -> {
                                val res = atan(arg)
                                if (isDegree) Math.toDegrees(res) else res
                            }
                            "log" -> log10(arg)
                            "ln" -> ln(arg)
                            "abs" -> abs(arg)
                            "e" -> exp(arg) // as a function e(x) -> equivalent to e^x
                            else -> throw RuntimeException("Unknown function: $func")
                        }
                    }
                }
            } else {
                throw RuntimeException("Unexpected: " + ch)
            }

            // check dynamic operations like powers ^, factorials ! and percentages %
            while (true) {
                if (eat('^')) {
                    x = x.pow(parseFactor(null))
                } else if (eat('!')) {
                    x = factorial(x)
                } else if (eat('%')) {
                    if (percentTarget != null) {
                        x = percentTarget * (x / 100.0)
                    } else {
                        x /= 100.0
                    }
                } else {
                    break
                }
            }

            return x
        }

        private fun factorial(n: Double): Double {
            if (n < 0.0) throw ArithmeticException("Factorial of negative number")
            val intN = n.roundToInt()
            if (abs(n - intN) > 1e-9) throw ArithmeticException("Factorial of non-integer")
            if (intN > 170) return Double.POSITIVE_INFINITY
            var result = 1.0
            for (i in 2..intN) {
                result *= i
            }
            return result
        }
    }
}
