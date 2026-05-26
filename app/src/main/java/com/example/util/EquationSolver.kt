package com.example.util

import kotlin.math.sqrt

object EquationSolver {

    data class SolutionStep(
        val title: String,
        val formula: String,
        val explanation: String
    )

    data class EquationResult(
        val roots: List<String>,
        val steps: List<SolutionStep>,
        val isError: Boolean = false,
        val errorMessage: String? = null
    )

    /**
     * Solves ax + b = c
     */
    fun solveLinear(a: Double, b: Double, c: Double): EquationResult {
        val steps = mutableListOf<SolutionStep>()
        
        steps.add(
            SolutionStep(
                title = "Identify coefficients",
                formula = "a = $a,  b = $b,  c = $c",
                explanation = "We are solving the linear equation: ax + b = c."
            )
        )

        if (a == 0.0) {
            if (b == c) {
                return EquationResult(
                    roots = listOf("All real numbers"),
                    steps = steps + SolutionStep(
                        title = "Checking values",
                        formula = "0·x + $b = $c ⟹ $b = $c",
                        explanation = "Since the coefficient of x is 0, and the remaining statement is always true, there are infinitely many solutions (any real number is a solution)."
                    )
                )
            } else {
                return EquationResult(
                    roots = emptyList(),
                    steps = steps + SolutionStep(
                        title = "Checking values",
                        formula = "0·x + $b = $c ⟹ $b ≠ $c",
                        explanation = "Since the coefficient of x is 0, and the remaining numbers do not equal each other ($b ≠ $c), this statement is a contradiction. There are no solutions."
                    ),
                    isError = true,
                    errorMessage = "No solution (contradiction)"
                )
            }
        }

        // Move b to RHS: ax = c - b
        val rhsValue = c - b
        steps.add(
            SolutionStep(
                title = "Subtract b from both sides",
                formula = "ax = c - b ⟹ $a·x = $c - ($b)",
                explanation = "In order to isolate the team with 'x', we subtract $b$ from both sides of the equation. This yields: $a·x = $rhsValue."
            )
        )

        // Divide by a: x = (c - b) / a
        val result = rhsValue / a
        steps.add(
            SolutionStep(
                title = "Divide by coefficient a",
                formula = "x = (c - b) / a ⟹ x = $rhsValue / $a",
                explanation = "Finally, we isolate 'x' completely by dividing both sides of the equation by the coefficient $a$. This yields our solved value: $result."
            )
        )

        return EquationResult(
            roots = listOf("x = %.4f".format(result)),
            steps = steps
        )
    }

    /**
     * Solves ax² + bx + c = 0
     */
    fun solveQuadratic(a: Double, b: Double, c: Double): EquationResult {
        val steps = mutableListOf<SolutionStep>()

        steps.add(
            SolutionStep(
                title = "Identify coefficients",
                formula = "a = $a,  b = $b,  c = $c",
                explanation = "We are solving a quadratic equation in standard form: ax² + bx + c = 0."
            )
        )

        // If a == 0, it reduces to bx + c = 0
        if (a == 0.0) {
            steps.add(
                SolutionStep(
                    title = "Linear reduction",
                    formula = "0·x² + $b·x + $c = 0 ⟹ $b·x + $c = 0",
                    explanation = "Since the coefficient of x² is 0, this is a linear equation: $b·x = -$c."
                )
            )
            if (b == 0.0) {
                if (c == 0.0) {
                    return EquationResult(
                        roots = listOf("All real numbers"),
                        steps = steps
                    )
                } else {
                    return EquationResult(
                        roots = emptyList(),
                        steps = steps,
                        isError = true,
                        errorMessage = "Contradiction: no solutions."
                    )
                }
            }
            val root = -c / b
            steps.add(
                SolutionStep(
                    title = "Solve for x",
                    formula = "x = -$c / $b",
                    explanation = "Solving this simple linear version yields x = $root."
                )
            )
            return EquationResult(
                roots = listOf("x = %.4f".format(root)),
                steps = steps
            )
        }

        // Calculate discriminant: D = b² - 4ac
        val disc = b * b - 4 * a * c
        steps.add(
            SolutionStep(
                title = "Calculate Discriminant (Δ)",
                formula = "Δ = b² - 4ac ⟹ Δ = ($b)² - 4·($a)·($c)",
                explanation = "The discriminant (Δ) tells us the nature of the roots. Here, Δ = ${b * b} - ${4 * a * c} = $disc."
            )
        )

        when {
            disc > 0.0 -> {
                val sqrtDisc = sqrt(disc)
                val root1 = (-b + sqrtDisc) / (2 * a)
                val root2 = (-b - sqrtDisc) / (2 * a)

                steps.add(
                    SolutionStep(
                        title = "Apply Quadratic Formula (Two Real Roots)",
                        formula = "x = (-b ± √Δ) / 2a",
                        explanation = "Since Δ > 0, we have two distinct real roots. We calculate √Δ = √$disc = %.4f.".format(sqrtDisc)
                    )
                )

                steps.add(
                    SolutionStep(
                        title = "Solve root 1 (+)",
                        formula = "x₁ = (-($b) + %.4f) / (2·$a)".format(sqrtDisc),
                        explanation = "Adding the square root of the discriminant yields the first root: %.4f.".format(root1)
                    )
                )

                steps.add(
                    SolutionStep(
                        title = "Solve root 2 (-)",
                        formula = "x₂ = (-($b) - %.4f) / (2·$a)".format(sqrtDisc),
                        explanation = "Subtracting the square root of the discriminant yields the second root: %.4f.".format(root2)
                    )
                )

                return EquationResult(
                    roots = listOf("x₁ = %.4f".format(root1), "x₂ = %.4f".format(root2)),
                    steps = steps
                )
            }
            disc == 0.0 -> {
                val root = -b / (2 * a)
                steps.add(
                    SolutionStep(
                        title = "Apply Quadratic Formula (One Real Root)",
                        formula = "x = -b / 2a",
                        explanation = "Since the discriminant (Δ) is exactly 0, there is only one distinct repeated real root. The square root of 0 is 0."
                    )
                )
                steps.add(
                    SolutionStep(
                        title = "Calculate root",
                        formula = "x = -($b) / (2·$a)",
                        explanation = "Evaluating the division yields: x = %.4f.".format(root)
                    )
                )
                return EquationResult(
                    roots = listOf("x = %.4f".format(root)),
                    steps = steps
                )
            }
            else -> {
                // disc < 0 (Complex roots)
                val rawReal = -b / (2 * a)
                val realPart = if (rawReal == 0.0) 0.0 else rawReal
                val rawImag = sqrt(-disc) / (2 * a)
                val imagPart = if (rawImag == 0.0) 0.0 else rawImag

                steps.add(
                    SolutionStep(
                        title = "Apply Quadratic Formula (Complex Roots)",
                        formula = "x = -b/2a ± i√( -Δ )/2a",
                        explanation = "Since Δ < 0, our roots are complex numbers. We find the real part: -b / 2a = %.4f, and the imaginary part: √(-Δ) / 2a = %.4f.".format(realPart, imagPart)
                    )
                )

                val root1Str = "x₁ = %.4f + %.4fi".format(realPart, imagPart)
                val root2Str = "x₂ = %.4f - %.4fi".format(realPart, imagPart)

                return EquationResult(
                    roots = listOf(root1Str, root2Str),
                    steps = steps
                )
            }
        }
    }
}
