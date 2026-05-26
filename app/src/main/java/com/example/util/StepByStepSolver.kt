package com.example.util

import kotlin.math.*

object StepByStepSolver {
    data class Solution(
        val title: String,
        val steps: List<String>,
        val finalResult: String
    )

    fun solve(expression: String, isDegree: Boolean): Solution? {
        val cleanExpr = expression.replace(" ", "")
        if (cleanExpr.isEmpty()) return null

        // 1. Sine
        if (cleanExpr.contains("sin(")) {
            val inner = extractInner(cleanExpr, "sin(") ?: return null
            val innerVal = try { MathParser.evaluate(inner, isDegree) } catch (e: Exception) { return null }
            val steps = mutableListOf<String>()
            steps.add("■ Task: Calculate the Sine of angle θ = $inner")
            if (inner != formatDouble(innerVal)) {
                steps.add("• Step 1: Simplify the angle argument first:\n  $inner = ${formatDouble(innerVal)}")
            }
            if (isDegree) {
                val rad = Math.toRadians(innerVal)
                steps.add("• Step 2: Since the calculator is in DEGREE mode, convert the angle to Radians inside the circular function:\n  θ_rad = θ_deg × (π / 180)\n  θ_rad = ${formatDouble(innerVal)} × (3.14159265 / 180) ≈ ${formatDouble(rad)} rad")
                val res = sin(rad)
                steps.add("• Step 3: Compute the trigonometric Sine of the radian angle value:\n  sin(${formatDouble(rad)} rad) ≈ ${formatDouble(res)}")
                return Solution("Sine Calculation (Degrees)", steps, formatDouble(res))
            } else {
                steps.add("• Step 2: Since the calculator is in RADIAN mode, compute the Sine value of the radian angle directly:\n  sin(${formatDouble(innerVal)} rad) ≈ ${formatDouble(sin(innerVal))}")
                return Solution("Sine Calculation (Radians)", steps, formatDouble(sin(innerVal)))
            }
        }

        // 2. Cosine
        if (cleanExpr.contains("cos(")) {
            val inner = extractInner(cleanExpr, "cos(") ?: return null
            val innerVal = try { MathParser.evaluate(inner, isDegree) } catch (e: Exception) { return null }
            val steps = mutableListOf<String>()
            steps.add("■ Task: Calculate the Cosine of angle θ = $inner")
            if (inner != formatDouble(innerVal)) {
                steps.add("• Step 1: Simplify the angle argument first:\n  $inner = ${formatDouble(innerVal)}")
            }
            if (isDegree) {
                val rad = Math.toRadians(innerVal)
                steps.add("• Step 2: Since the calculator is in DEGREE mode, convert the angle to Radians inside the circular function:\n  θ_rad = θ_deg × (π / 180)\n  θ_rad = ${formatDouble(innerVal)} × (3.14159265 / 180) ≈ ${formatDouble(rad)} rad")
                val res = cos(rad)
                steps.add("• Step 3: Compute the trigonometric Cosine of the radian angle value:\n  cos(${formatDouble(rad)} rad) ≈ ${formatDouble(res)}")
                return Solution("Cosine Calculation (Degrees)", steps, formatDouble(res))
            } else {
                steps.add("• Step 2: Since the calculator is in RADIAN mode, compute the Cosine value of the radian angle directly:\n  cos(${formatDouble(innerVal)} rad) ≈ ${formatDouble(cos(innerVal))}")
                return Solution("Cosine Calculation (Radians)", steps, formatDouble(cos(innerVal)))
            }
        }

        // 3. Tangent
        if (cleanExpr.contains("tan(")) {
            val inner = extractInner(cleanExpr, "tan(") ?: return null
            val innerVal = try { MathParser.evaluate(inner, isDegree) } catch (e: Exception) { return null }
            val steps = mutableListOf<String>()
            steps.add("■ Task: Calculate the Tangent of angle θ = $inner")
            if (inner != formatDouble(innerVal)) {
                steps.add("• Step 1: Simplify the angle argument first:\n  $inner = ${formatDouble(innerVal)}")
            }
            if (isDegree) {
                val rad = Math.toRadians(innerVal)
                steps.add("• Step 2: Since the calculator is in DEGREE mode, convert the angle to Radians inside the circular function:\n  θ_rad = θ_deg × (π / 180)\n  θ_rad = ${formatDouble(innerVal)} × (3.14159265 / 180) ≈ ${formatDouble(rad)} rad")
                val res = tan(rad)
                steps.add("• Step 3: Compute the trigonometric Tangent of the radian angle value:\n  tan(${formatDouble(rad)} rad) ≈ ${formatDouble(res)}")
                return Solution("Tangent Calculation (Degrees)", steps, formatDouble(res))
            } else {
                steps.add("• Step 2: Since the calculator is in RADIAN mode, compute the Tangent value of the radian angle directly:\n  tan(${formatDouble(innerVal)} rad) ≈ ${formatDouble(tan(innerVal))}")
                return Solution("Tangent Calculation (Radians)", steps, formatDouble(tan(innerVal)))
            }
        }

        // 4. Inverse Sine (sin⁻¹ or asin)
        if (cleanExpr.contains("sin⁻¹(") || cleanExpr.contains("asin(")) {
            val prefix = if (cleanExpr.contains("sin⁻¹(")) "sin⁻¹(" else "asin("
            val inner = extractInner(cleanExpr, prefix) ?: return null
            val innerVal = try { MathParser.evaluate(inner, isDegree) } catch (e: Exception) { return null }
            val steps = mutableListOf<String>()
            steps.add("■ Task: Calculate the Inverse Sine (arcsin) of value y = $inner")
            if (inner != formatDouble(innerVal)) {
                steps.add("• Step 1: Simplify the value argument first:\n  $inner = ${formatDouble(innerVal)}")
            }
            if (innerVal < -1.0 || innerVal > 1.0) {
                steps.add("• Step 2: Check input boundaries. Arcsin input must be between -1 and 1 inclusive.\n  Value is ${formatDouble(innerVal)}, which is OUT of domain.")
                return Solution("Inverse Sine (Arcsin) Error", steps, "Error: Domain [-1, 1]")
            }
            val radRes = asin(innerVal)
            steps.add("• Step 2: Compute inverse circular sine yielding radians:\n  θ_rad = arcsin(${formatDouble(innerVal)}) ≈ ${formatDouble(radRes)} rad")
            if (isDegree) {
                val degRes = Math.toDegrees(radRes)
                steps.add("• Step 3: Convert the radian outcome to Degrees (mode is DEG):\n  θ_deg = θ_rad × (180 / π)\n  θ_deg = ${formatDouble(radRes)} × (180 / 3.14159265) ≈ ${formatDouble(degRes)}°")
                return Solution("Inverse Sine (Degrees)", steps, "${formatDouble(degRes)}°")
            } else {
                return Solution("Inverse Sine (Radians)", steps, "${formatDouble(radRes)} rad")
            }
        }

        // 5. Inverse Cosine (cos⁻¹ or acos)
        if (cleanExpr.contains("cos⁻¹(") || cleanExpr.contains("acos(")) {
            val prefix = if (cleanExpr.contains("cos⁻¹(")) "cos⁻¹(" else "acos("
            val inner = extractInner(cleanExpr, prefix) ?: return null
            val innerVal = try { MathParser.evaluate(inner, isDegree) } catch (e: Exception) { return null }
            val steps = mutableListOf<String>()
            steps.add("■ Task: Calculate the Inverse Cosine (arccos) of value y = $inner")
            if (inner != formatDouble(innerVal)) {
                steps.add("• Step 1: Simplify the value argument first:\n  $inner = ${formatDouble(innerVal)}")
            }
            if (innerVal < -1.0 || innerVal > 1.0) {
                steps.add("• Step 2: Check input boundaries. Arccos input must be between -1 and 1 inclusive.\n  Value is ${formatDouble(innerVal)}, which is OUT of domain.")
                return Solution("Inverse Cosine (Arccos) Error", steps, "Error: Domain [-1, 1]")
            }
            val radRes = acos(innerVal)
            steps.add("• Step 2: Compute inverse circular cosine yielding radians:\n  θ_rad = arccos(${formatDouble(innerVal)}) ≈ ${formatDouble(radRes)} rad")
            if (isDegree) {
                val degRes = Math.toDegrees(radRes)
                steps.add("• Step 3: Convert the radian outcome to Degrees (mode is DEG):\n  θ_deg = θ_rad × (180 / π)\n  θ_deg = ${formatDouble(radRes)} × (180 / 3.14159265) ≈ ${formatDouble(degRes)}°")
                return Solution("Inverse Cosine (Degrees)", steps, "${formatDouble(degRes)}°")
            } else {
                return Solution("Inverse Cosine (Radians)", steps, "${formatDouble(radRes)} rad")
            }
        }

        // 6. Inverse Tangent (tan⁻¹ or atan)
        if (cleanExpr.contains("tan⁻¹(") || cleanExpr.contains("atan(")) {
            val prefix = if (cleanExpr.contains("tan⁻¹(")) "tan⁻¹(" else "atan("
            val inner = extractInner(cleanExpr, prefix) ?: return null
            val innerVal = try { MathParser.evaluate(inner, isDegree) } catch (e: Exception) { return null }
            val steps = mutableListOf<String>()
            steps.add("■ Task: Calculate the Inverse Tangent (arctan) of value y = $inner")
            if (inner != formatDouble(innerVal)) {
                steps.add("• Step 1: Simplify the value argument first:\n  $inner = ${formatDouble(innerVal)}")
            }
            val radRes = atan(innerVal)
            steps.add("• Step 2: Compute inverse circular tangent yielding radians:\n  θ_rad = arctan(${formatDouble(innerVal)}) ≈ ${formatDouble(radRes)} rad")
            if (isDegree) {
                val degRes = Math.toDegrees(radRes)
                steps.add("• Step 3: Convert the radian outcome to Degrees (mode is DEG):\n  θ_deg = θ_rad × (180 / π)\n  θ_deg = ${formatDouble(radRes)} × (180 / 3.14159265) ≈ ${formatDouble(degRes)}°")
                return Solution("Inverse Tangent (Degrees)", steps, "${formatDouble(degRes)}°")
            } else {
                return Solution("Inverse Tangent (Radians)", steps, "${formatDouble(radRes)} rad")
            }
        }

        // 7. Log Base 10
        if (cleanExpr.contains("log(")) {
            val inner = extractInner(cleanExpr, "log(") ?: return null
            val innerVal = try { MathParser.evaluate(inner, isDegree) } catch (e: Exception) { return null }
            val steps = mutableListOf<String>()
            steps.add("■ Task: Calculate common logarithm log₁₀($inner)")
            if (inner != formatDouble(innerVal)) {
                steps.add("• Step 1: Simplify the value argument first:\n  $inner = ${formatDouble(innerVal)}")
            }
            if (innerVal <= 0.0) {
                steps.add("• Step 2: Check domain constraint. Logarithms can only be evaluated for positive values (>0).\n  Value is ${formatDouble(innerVal)} which is non-positive.")
                return Solution("Common Logarithm Error", steps, "Error: Domain (>0)")
            }
            val res = log10(innerVal)
            steps.add("• Step 2: Apply base 10 logarithmic resolution:\n  log₁₀(${formatDouble(innerVal)}) ≈ ${formatDouble(res)}")
            return Solution("Common Logarithm (Base 10)", steps, formatDouble(res))
        }

        // 8. Natural Log (ln)
        if (cleanExpr.contains("ln(")) {
            val inner = extractInner(cleanExpr, "ln(") ?: return null
            val innerVal = try { MathParser.evaluate(inner, isDegree) } catch (e: Exception) { return null }
            val steps = mutableListOf<String>()
            steps.add("■ Task: Calculate natural logarithm ln($inner)")
            if (inner != formatDouble(innerVal)) {
                steps.add("• Step 1: Simplify the value argument first:\n  $inner = ${formatDouble(innerVal)}")
            }
            if (innerVal <= 0.0) {
                steps.add("• Step 2: Check domain constraint. Natural logarithm can only be evaluated for positive values (>0).\n  Value is ${formatDouble(innerVal)} which is non-positive.")
                return Solution("Natural Logarithm Error", steps, "Error: Domain (>0)")
            }
            val res = ln(innerVal)
            steps.add("• Step 2: Apply base e (~2.71828) logarithmic resolution:\n  ln(${formatDouble(innerVal)}) ≈ ${formatDouble(res)}")
            return Solution("Natural Logarithm (Base e)", steps, formatDouble(res))
        }

        // 9. Log base (logbase(b,x) or logN(b,x))
        if (cleanExpr.contains("logbase(") || cleanExpr.contains("logN(")) {
            val prefix = if (cleanExpr.contains("logbase(")) "logbase(" else "logN("
            val inner = extractInner(cleanExpr, prefix) ?: return null
            val parts = splitByTopLevelComma(inner)
            if (parts.size != 2) return null
            val baseVal = try { MathParser.evaluate(parts[0], isDegree) } catch (e: Exception) { return null }
            val xVal = try { MathParser.evaluate(parts[1], isDegree) } catch (e: Exception) { return null }
            val steps = mutableListOf<String>()
            steps.add("■ Task: Calculate change-of-base logarithm: log_{${formatDouble(baseVal)}}(${formatDouble(xVal)})")
            steps.add("• Step 1: Divide into basic and target terms:\n  Base b = ${formatDouble(baseVal)}\n  Argument x = ${formatDouble(xVal)}")
            if (baseVal <= 0.0 || baseVal == 1.0 || xVal <= 0.0) {
                steps.add("• Step 2: Verify domain validity. Log base must be >0 and ≠1, and argument must be >0.")
                return Solution("Log Base N Error", steps, "Error: Domain violation")
            }
            val lnX = ln(xVal)
            val lnBase = ln(baseVal)
            val finalRes = lnX / lnBase
            steps.add("• Step 2: Apply log change-of-base formula:\n  log_b(x) = ln(x) / ln(b)")
            steps.add("  ln(${formatDouble(xVal)}) ≈ ${formatDouble(lnX)}")
            steps.add("  ln(${formatDouble(baseVal)}) ≈ ${formatDouble(lnBase)}")
            steps.add("  log_b(x) ≈ ${formatDouble(lnX)} / ${formatDouble(lnBase)} ≈ ${formatDouble(finalRes)}")
            return Solution("Change-of-Base Logarithm", steps, formatDouble(finalRes))
        }

        // 10. Exponential e
        if (cleanExpr.contains("e(")) {
            val inner = extractInner(cleanExpr, "e(") ?: return null
            val innerVal = try { MathParser.evaluate(inner, isDegree) } catch (e: Exception) { return null }
            val steps = mutableListOf<String>()
            steps.add("■ Task: Calculate exponential power of constant e: e^($inner)")
            if (inner != formatDouble(innerVal)) {
                steps.add("• Step 1: Simplify the exponent argument first:\n  x = $inner = ${formatDouble(innerVal)}")
            }
            val res = exp(innerVal)
            steps.add("• Step 2: Raise mathematical constant e (~2.71828) to power x:\n  e^${formatDouble(innerVal)} ≈ ${formatDouble(res)}")
            return Solution("Exponential Calculation", steps, formatDouble(res))
        }

        // 11. Powers ^
        if (cleanExpr.contains("^")) {
            val parts = splitByTopLevelPower(cleanExpr)
            if (parts.size != 2) return null
            val baseVal = try { MathParser.evaluate(parts[0], isDegree) } catch (e: Exception) { return null }
            val exponentVal = try { MathParser.evaluate(parts[1], isDegree) } catch (e: Exception) { return null }
            val steps = mutableListOf<String>()
            steps.add("■ Task: Resolve exponential power: ${formatDouble(baseVal)} ^ ${formatDouble(exponentVal)}")
            steps.add("• Step 1: Base (x) = ${formatDouble(baseVal)}, Exponent (y) = ${formatDouble(exponentVal)}")
            val res = baseVal.pow(exponentVal)
            steps.add("• Step 2: Compute power value:\n  x^y = ${formatDouble(baseVal)}^${formatDouble(exponentVal)} ≈ ${formatDouble(res)}")
            return Solution("Power Calculation", steps, formatDouble(res))
        }

        // 12. Sqrt √
        if (cleanExpr.contains("sqrt(") || cleanExpr.contains("√(")) {
            val prefix = if (cleanExpr.contains("sqrt(")) "sqrt(" else "√("
            val inner = extractInner(cleanExpr, prefix) ?: return null
            val innerVal = try { MathParser.evaluate(inner, isDegree) } catch (e: Exception) { return null }
            val steps = mutableListOf<String>()
            steps.add("■ Task: Calculate square root: √($inner)")
            if (inner != formatDouble(innerVal)) {
                steps.add("• Step 1: Simplify the argument first:\n  $inner = ${formatDouble(innerVal)}")
            }
            if (innerVal < 0.0) {
                steps.add("• Step 2: Check domain compatibility. Square root of a negative number produces complex outputs.")
                return Solution("Square Root Error", steps, "Error: Domain (>=0)")
            }
            val res = sqrt(innerVal)
            steps.add("• Step 2: Extract real square root:\n  √${formatDouble(innerVal)} ≈ ${formatDouble(res)}")
            return Solution("Square Root Resolution", steps, formatDouble(res))
        }

        return null
    }

    private fun extractInner(expr: String, prefix: String): String? {
        val startIdx = expr.indexOf(prefix)
        if (startIdx == -1) return null
        val innerStart = startIdx + prefix.length
        
        var depth = 1
        var endIdx = innerStart
        while (endIdx < expr.length && depth > 0) {
            val char = expr[endIdx]
            if (char == '(') depth++
            else if (char == ')') depth--
            if (depth == 0) break
            endIdx++
        }
        if (depth > 0) return expr.substring(innerStart) // Unbalanced fallback
        return expr.substring(innerStart, endIdx)
    }

    private fun splitByTopLevelComma(str: String): List<String> {
        val list = mutableListOf<String>()
        var depth = 0
        var current = StringBuilder()
        for (i in 0 until str.length) {
            val char = str[i]
            if (char == '(') depth++
            else if (char == ')') depth--
            
            if (char == ',' && depth == 0) {
                list.add(current.toString())
                current = StringBuilder()
            } else {
                current.append(char)
            }
        }
        list.add(current.toString())
        return list
    }

    private fun splitByTopLevelPower(str: String): List<String> {
        val idx = str.indexOf("^")
        if (idx == -1) return emptyList()
        return listOf(str.substring(0, idx), str.substring(idx + 1))
    }

    private fun formatDouble(num: Double): String {
        return if (num.isInfinite()) "Infinity"
        else if (num.isNaN()) "NaN"
        else if (num % 1.0 == 0.0) num.toLong().toString()
        else "%.5f".format(num).trimEnd('0').trimEnd('.')
    }
}
